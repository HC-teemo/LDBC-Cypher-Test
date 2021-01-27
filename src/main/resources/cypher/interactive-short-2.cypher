MATCH (:person {id:"$personId"})<-[:hasCreator]-(m)-[:replyOf]->(p:post)
MATCH (p)-[:hasCreator]->(c)
RETURN
  m.id AS messageId,
  m.creationDate AS messageCreationDate,
  p.id AS originalPostId,
  c.id AS originalPostAuthorId,
  c.firstName AS originalPostAuthorFirstName,
  c.lastName AS originalPostAuthorLastName
LIMIT 10