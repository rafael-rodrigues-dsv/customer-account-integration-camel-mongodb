--===============================================
-- select * from registration.customer c;
--===============================================
db.customer.find()

--===============================================
-- select count(*) from registration.customer c where c.status = 2;
--===============================================
db.customer.find({ "customerStatus": "IMPORTED" }).count()

--===============================================
-- DELETE FROM registration.customer c
-- WHERE c.customer_id IN (
--   SELECT customer_id
--   FROM registration.customer
--   ORDER BY customer_id
--   LIMIT 5000
-- );
--===============================================
var customersToDelete = db.customer.find().limit(5000);
customersToDelete.forEach(function(customer) {
    db.customer.remove({ "_id": customer._id });
});

--===============================================
-- select 	min(c.creation_date) as inicial_date,
-- 		max(c.creation_date) as final_date,
-- 		count(c.customer_id) as count
-- from registration.customer c;
--===============================================
db.customer.aggregate([
    {
        $group: {
            _id: null,
            inicialDate: { $min: "$creationDate" },
            finalDate: { $max: "$creationDate" },
            count: { $sum: 1 }
        }
    }
])

--===============================================
-- update registration.customer set status = 1 WHERE customer_id IN (
--   SELECT customer_id
--   FROM registration.customer
--   ORDER BY customer_id
--   LIMIT 5000
-- );
--===============================================
var customersToUpdate = db.customer.find().limit(5000);
customersToUpdate.forEach(function(customer) {
    db.customer.update({ "_id": customer._id }, { $set: { "customerStatus": 1 } });
});