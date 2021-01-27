MATCH (m:comment {id:"$commentId"})
RETURN
  m.creationDate AS messageCreationDate,
  m.content as content