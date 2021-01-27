MATCH (n:person {id:"$personId"})-[r:knows]-(friend)
RETURN
  friend.id AS personId,
  friend.firstName AS firstName,
  friend.lastName AS lastName,
  r.creationDate AS friendshipCreationDate