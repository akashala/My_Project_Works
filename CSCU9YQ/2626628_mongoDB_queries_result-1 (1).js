// Q.1.1
db.movies.aggregate([{"$group":{_id: "$genres", avg_runtime:{$avg:"$runtime"}}}, {"$sort": {"avg_runtime": -1}}, {"$limit": 5}])


// Q.1.2
db.movies.aggregate([ {"$match": {"$and":[{countries: "UK"}]}}, {"$group":{_id: "$countries", count: {$sum: 1}}}, {"$sort": {"count": -1}}])


// Q.2.1
db.movies.find({"genres": "Sport"}).count()


// Q.2.2
db.movies.aggregate([{"$match": {"genres": "Sport"}},{"$sort": {"imdb.rating": -1}}, {"$limit": 3}, {"$project": {"title": 1, "_id": 0, "year": 1, "imdb.rating": 1, "imdb.votes": 1}}])


// Q.3.1
db.movies.aggregate([{"$project":{my_rating:{$avg:["$awards.wins", "$awards.nominations"]},"_id":0, "title": 1}}, {"$sort": {"my_rating": -1}}])


// Q.3.2
db.movies.aggregate([{ $match: {"genres": "Sport" }}, {"$project":{my_rating:{$avg:["$awards.wins", "$awards.nominations"]},"_id":0, "title": 1}}, {"$sort": {"my_rating": -1}}, {"$limit": 3}])