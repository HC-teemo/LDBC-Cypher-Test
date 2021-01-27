MATCH (m:comment{id:"$commentId"})-[:replyOf]->(p:post)<-[:containerOf]-(f:forum)-[:hasModerator]->(mod:person)
RETURN
  f.id AS forumId,
  f.title AS forumTitle,
  mod.id AS moderatorId,
  mod.firstName AS moderatorFirstName,
  mod.lastName AS moderatorLastName