requestBody(requestBody) ::= <<
requestBody:
    required: <requestBody.required>
    content:
      <requestBody.content.contentType.keys:{ k | <k>:
  schema:
    $ref: '<requestBody.content.contentType.(k)>'}>
>>


