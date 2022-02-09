package org.platonos.rest.build.maven

import org.apache.maven.artifact.resolver.filter.ArtifactFilter
import org.apache.maven.artifact.resolver.filter.ScopeArtifactFilter
import org.apache.maven.plugin.AbstractMojo
import org.apache.maven.plugins.annotations.Component
import org.apache.maven.plugins.annotations.LifecyclePhase
import org.apache.maven.plugins.annotations.Mojo
import org.apache.maven.plugins.annotations.Parameter
import org.apache.maven.project.DefaultProjectBuildingRequest
import org.apache.maven.project.MavenProject
import org.apache.maven.shared.dependency.graph.DependencyGraphBuilder
import org.apache.maven.shared.dependency.graph.DependencyNode
import org.eclipse.aether.DefaultRepositorySystemSession
import org.eclipse.aether.RepositorySystem
import org.eclipse.aether.RepositorySystemSession
import org.eclipse.aether.artifact.Artifact
import org.eclipse.aether.artifact.DefaultArtifact
import org.eclipse.aether.resolution.ArtifactRequest
import org.eclipse.aether.resolution.ArtifactResolutionException
import org.platonos.rest.generate.Build
import org.platonos.rest.generate.ProjectInfo
import org.platonos.rest.openapi.Options
import org.platonos.rest.generate2.util.Logger
import org.platonos.rest.generate2.Generator2
import java.io.File

@Mojo(
    name = "generate",
    defaultPhase = LifecyclePhase.GENERATE_SOURCES
)
class PlatonosRestMojo : AbstractMojo() {

    @Parameter(defaultValue = "\${project}", required = true, readonly = true)
    private lateinit var project: MavenProject

    @Component( hint = "default" )
    private lateinit var dependencyGraphBuilder: DependencyGraphBuilder

    @Component
    private lateinit var repoSystem: RepositorySystem

    @Parameter(defaultValue = "\${repositorySystemSession}")
    private val repoSession: RepositorySystemSession? = null

    private lateinit var repositorySystemSession: RepositorySystemSession

    @Parameter(defaultValue = "\${project.build.directory}/generated-sources", required = true)
    private lateinit var generatedSourceDirectory: File

    @Parameter(property = "model-package", required = false)
    private var modelPackage: String? = null

    @Parameter(property = "api-package", required = false)
    private var apiPackageName: String? = null

    @Parameter(property = "openApiFile", required = true)
    private var openApiFile: String = ""

    @Parameter(property = "generateModels", required = false, defaultValue = "true")
    private var generateModels = true

    @Parameter(property = "generateApiDefintions", required = false, defaultValue = "false")
    private var generateApiDefintions = false

    @Parameter(property = "generateApiImplementations", required = false, defaultValue = "false")
    private var generateApiImplementations = false

    @Parameter(property = "dynamicModels", required = false)
    private var dynamicModels: String? = null

    @Parameter(property = "features", required = false)
    private var features: Map<String, Boolean> = emptyMap()

    override fun execute() {
        repositorySystemSession = DefaultRepositorySystemSession(repoSession)

        if (openApiFile.isEmpty()) {
            log.warn(""" 
                | No files specified to process.
                | Specify a file via configuration of the plugin.
                | Files must have a yml or yaml file extension.
                | 
                | <configuration>
                |   <openApiFile>openapi.yml</openApiFiles>
                | </configuration>
            """.trimMargin())
        }

        Logger.setLoggerFactory(MavenLoggerFactory(this))

        val options = Options(
            fileName = openApiFile,
            modelPackageName = modelPackage,
            apiPackageName = apiPackageName,
            generateModels = generateModels,
            generateApiDefintions = generateApiDefintions,
            generateApiImplementations = generateApiImplementations,
            dynamicModels = parseDynamicModels(),
            features = features
        )

        val build = Build(generatedSourceDirectory)

        val projectInfo = ProjectInfo(
            this.project.compileSourceRoots,
            resolveDependencies(),
            build
        )

        val generator2 = Generator2(options, projectInfo)
        generator2.execute()
    }

    private fun resolveDependencies(): List<String> {
        val dependencies = mutableListOf<String>()

        val buildingRequest = DefaultProjectBuildingRequest(DefaultProjectBuildingRequest())
        buildingRequest.repositorySession = repositorySystemSession
        buildingRequest.project = project

        val artifactFilter: ArtifactFilter = ScopeArtifactFilter("compile")

        val rootNode = dependencyGraphBuilder.buildDependencyGraph( buildingRequest, artifactFilter)

        rootNode.children.forEach { child ->
            visitChild(child, dependencies)
        }

        return dependencies
    }

    private fun visitChild(node: DependencyNode, dependencies: MutableList<String>) {
        val artifactRequest = createArtifactRequest(
            node.artifact.groupId,
            node.artifact.artifactId,
            node.artifact.type,
            node.artifact.version
        )

        val artifact = resolveArtifact(artifactRequest)

        if (artifact?.file != null) {
            dependencies.add(artifact.file.absolutePath)
        }

        node.children.forEach { child ->
            visitChild(child, dependencies)
        }
    }

    private fun createArtifactRequest(groupId: String,
                                      artifactId: String,
                                      type: String,
                                      version: String): ArtifactRequest {
        val artifactRequest = ArtifactRequest()
        artifactRequest.artifact = DefaultArtifact(
            groupId,
            artifactId,
            type,
            version
        )
        return artifactRequest
    }

    private fun resolveArtifact(artifactRequest: ArtifactRequest): Artifact? {
        return try {
            val artifactResults = this.repoSystem.resolveArtifacts(this.repoSession, listOf(artifactRequest))
            artifactResults[0].artifact
        } catch (e: ArtifactResolutionException) {
            null
        }
    }


    private fun parseDynamicModels(): List<String> {
        val dModels = dynamicModels

        if (dModels == null) {
            return emptyList()
        } else {
            return dModels.replace("\r", "")
                .split("\n")
                .toList()
        }
    }
}