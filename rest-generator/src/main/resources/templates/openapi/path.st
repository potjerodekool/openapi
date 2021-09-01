path(path) ::= <<
<path.path>:
  <if(path.get)>get:
  <path.post:apiOperation()><endif>
  <if(path.post)>post:
  <path.post:apiOperation()><endif>
  <if(path.put)>put:
  <path.post:apiOperation()><endif>
  <if(path.patch)>patch:
  <path.post:apiOperation()><endif>
  <if(path.delete)>delete:
  <path.post:apiOperation()><endif>
>>