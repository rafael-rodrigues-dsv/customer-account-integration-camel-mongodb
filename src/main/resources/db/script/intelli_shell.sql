--===============================================
-- select * from registration.customer c;
--===============================================
db.customer.find()

db.customer.find({
    customerStatus: { $in: ["IMPORTED", "PROCESSING", "SUCCESSFUL_PROCESSING", "PROCESSING_FAILURE"] }
});

--===============================================
-- delete from registration.customer c;
--===============================================
db.getCollection("customer").deleteMany({})

--===============================================
-- select
-- 		count(c.customer_id) as count,
--      count(c.status) as status
-- from registration.customer c group by c.status;
--===============================================
db.getCollection("customer").aggregate([
    {
        $group: {
            _id: "$customerStatus",
            count: { $sum: 1 }
        }
    }
])

--===============================================
-- update registration.customer set status = 1 WHERE customer_id IN (
--   SELECT customer_id
--   FROM registration.customer
--   WHERE customer.status in(PROCESSING, SUCCESSFUL_PROCESSING, PROCESSING_FAILURE)
--   ORDER BY customer_id
-- );
--===============================================
var customersToUpdate = db.customer.find({
    customerStatus: { $in: ["IMPORTED"] }
}).toArray();

var customerIdsToUpdate = customersToUpdate.map(function(customer) {
    return customer._id;
});

db.customer.updateMany(
    { "_id": { $in: customerIdsToUpdate } },
    { $set: { "customerStatus": "PROCESSING" }, $unset: { "errorMessage": "" } }
);