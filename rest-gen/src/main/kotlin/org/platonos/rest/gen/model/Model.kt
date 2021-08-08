package org.platonos.rest.gen.model

class Model(val qualifiedName: String) {

    val packageName: String?
    val simpleName: String
    private val properties = mutableListOf<ModelProperty>()

    init {
        val sep = qualifiedName.lastIndexOf('.')

        if (sep < 0) {
            packageName = null
            simpleName = qualifiedName
        } else {
            packageName = qualifiedName.substring(0, sep)
            simpleName = qualifiedName.substring(sep + 1)
        }
    }

    val hasPackageName = packageName != null

    fun getProperties(): List<ModelProperty> {
        return properties
    }

    fun addProperty(property: ModelProperty) {
        properties += property
    }

}