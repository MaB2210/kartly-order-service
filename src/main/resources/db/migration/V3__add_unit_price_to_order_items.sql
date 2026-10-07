ALTER TABLE order_items ADD COLUMN unit_price NUMERIC(38,2);

UPDATE order_items oi
SET unit_price = o.total_amount / oi.quantity
FROM orders o
WHERE oi.order_id = o.id
  AND oi.quantity > 0
  AND (SELECT COUNT(*) FROM order_items x WHERE x.order_id = o.id) = 1;