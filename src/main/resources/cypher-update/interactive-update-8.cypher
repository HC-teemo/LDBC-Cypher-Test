MATCH (p1:Person {id:$personId}), (p2:Person {id:$personId})
CREATE (p1)-[:KNOWS {creationDate:$creationDate}]->(p2)
