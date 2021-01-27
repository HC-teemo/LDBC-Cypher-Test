MATCH (m:post {id:"$postId"})-[:hasCreator]->(p:person)
RETURN
  p.id AS personId,
  p.firstName AS firstName,
  p.lastName AS lastName