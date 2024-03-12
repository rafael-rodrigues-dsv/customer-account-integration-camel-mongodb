--===============================================
-- select * from registration.customer c;
--===============================================
db.customer.find()

--===============================================
-- select count(*) from registration.customer c where c.status = 2;
--===============================================
db.customer.aggregate([
    { $match: { "customerStatus": "IMPORTED" } },
    { $group: { _id: null, count: { $sum: 1 } } }
]).toArray();

db.customer.aggregate([
    { $match: { "customerStatus": "SUCCESSFUL_PROCESSING" } },
    { $group: { _id: null, count: { $sum: 1 } } }
]).toArray();

--===============================================
-- DELETE FROM registration.customer c
-- WHERE c.customer_id IN (
--   SELECT customer_id
--   FROM registration.customer
--   ORDER BY customer_id
--   LIMIT 5000
-- );
--===============================================
var customersToDelete = db.customer.find().limit(5000).toArray();
var customerIdsToDelete = customersToDelete.map(function(customer) {
    return customer._id;
});

db.customer.deleteMany({ "_id": { $in: customerIdsToDelete } });

--===============================================
-- select 	min(c.creation_date) as inicial_date,
-- 		max(c.creation_date) as final_date,
-- 		count(c.customer_id) as count
-- from registration.customer c;
--===============================================
db.customer.aggregate([
    {
        $group: {
            _id: "$customerStatus",
            inicialDate: { $min: "$creationDate" },
            finalDate: { $max: "$creationDate" },
            count: { $sum: 1 }
        }
    },
    {
        $group: {
            _id: null,
            statusCounts: {
                $push: {
                    customerStatus: "$_id",
                    count: "$count"
                }
            },
            inicialDate: { $min: "$inicialDate" },
            finalDate: { $max: "$finalDate" },
            totalCount: { $sum: "$count" }
        }
    }
]);

--===============================================
-- update registration.customer set status = 1 WHERE customer_id IN (
--   SELECT customer_id
--   FROM registration.customer
--   ORDER BY customer_id
--   LIMIT 5000
-- );
--===============================================
var customersToUpdate = db.customer.find().limit(5000).toArray();
var customerIdsToUpdate = customersToUpdate.map(function(customer) {
    return customer._id;
});

db.customer.updateMany(
    { "_id": { $in: customerIdsToUpdate } },
    { $set: { "customerStatus": "PROCESSING" } }
);
