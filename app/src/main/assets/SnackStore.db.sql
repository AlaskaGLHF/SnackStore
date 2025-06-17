BEGIN TRANSACTION;
CREATE TABLE IF NOT EXISTS "Clients" (
	"id"	INTEGER,
	"first_name"	TEXT,
	"second_name"	TEXT,
	"third_name"	TEXT,
	"birth_day"	TEXT,
	"phone_number"	TEXT,
	"email"	TEXT,
	PRIMARY KEY("id" AUTOINCREMENT)
);
CREATE TABLE IF NOT EXISTS "Delivery_Driver" (
	"id"	INTEGER,
	"first_name"	TEXT,
	"second_name"	TEXT,
	"third_name"	TEXT,
	"phone_number"	TEXT,
	"email"	TEXT,
	PRIMARY KEY("id" AUTOINCREMENT)
);
CREATE TABLE IF NOT EXISTS "Favorite_Goods" (
	"id"	INTEGER,
	"client_id"	INTEGER,
	"goods_id"	INTEGER,
	PRIMARY KEY("id" AUTOINCREMENT),
	FOREIGN KEY("client_id") REFERENCES "Clients"("id"),
	FOREIGN KEY("goods_id") REFERENCES "Goods"("id")
);
CREATE TABLE IF NOT EXISTS "Goods" (
	"id"	INTEGER,
	"name"	TEXT,
	"description"	TEXT,
	"price"	TEXT,
	"image_path"	TEXT,
	"discount"	INTEGER,
	PRIMARY KEY("id" AUTOINCREMENT)
);
CREATE TABLE IF NOT EXISTS "Goods_Tags" (
	"id"	INTEGER,
	"good_id"	INTEGER,
	"tag"	TEXT,
	PRIMARY KEY("id" AUTOINCREMENT),
	FOREIGN KEY("good_id") REFERENCES "Goods"("id")
);
CREATE TABLE IF NOT EXISTS "Ordered_Goods" (
	"id"	INTEGER,
	"order_id"	INTEGER,
	"good_id"	INTEGER,
	PRIMARY KEY("id" AUTOINCREMENT),
	FOREIGN KEY("good_id") REFERENCES "Goods"("id"),
	FOREIGN KEY("order_id") REFERENCES "Orders"("id")
);
CREATE TABLE IF NOT EXISTS "Orders" (
	"id"	INTEGER,
	"client_id"	INTEGER,
	"address"	TEXT,
	"delivery_driver_id"	INTEGER,
	"date"	TEXT,
	PRIMARY KEY("id" AUTOINCREMENT),
	FOREIGN KEY("client_id") REFERENCES "Clients"("id"),
	FOREIGN KEY("delivery_driver_id") REFERENCES "Delivery_Driver"("id")
);
CREATE TABLE IF NOT EXISTS "CartItem" (
    "id" INTEGER PRIMARY KEY AUTOINCREMENT,
    "client_id" INTEGER NOT NULL,
    "goods_id" INTEGER NOT NULL,
    "quantity" INTEGER NOT NULL DEFAULT 1,
    FOREIGN KEY("client_id") REFERENCES "Client"("id"),
    FOREIGN KEY("goods_id") REFERENCES "Goods"("id")
);
COMMIT;
