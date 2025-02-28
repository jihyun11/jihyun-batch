# jihyun-batch

## 12월 17일 6주차 과제

### create user table

```sql
CREATE TABLE public."user"
(
    id     bigserial NOT NULL,
    "name" varchar NULL,
    status varchar NULL,
    CONSTRAINT user_pkey PRIMARY KEY (id)
);
```

### create user_image table

```sql
CREATE TABLE public.user_image
(
    id      bigserial NOT NULL,
    user_id bigserial NOT NULL,
    CONSTRAINT user_image_pkey PRIMARY KEY (id),
    CONSTRAINT fk_user_id FOREIGN KEY (user_id)
        REFERENCES public."user" (id)
        ON DELETE CASCADE
);
```

### select table

```sql
select count(*)
from "user" u
where status = 'INACTIVE';

select count(*)
from user_image ui;
```

--- 

### drop table

```sql
drop table "user";
drop table user_image;

delete
from "user";
delete
from user_image;
```

### sequence delete

```sql
ALTER SEQUENCE public.user_id_seq
    RESTART 1;
ALTER SEQUENCE public.user_image_id_seq
    RESTART 1;
```

## 1월 5일

```sql
CREATE TABLE public.product
(
    id     bigserial NOT NULL,
    "name" varchar   NOT NULL,
    status varchar   NOT NULL,
    "date" date      NOT NULL
);

ALTER TABLE product
    ADD CONSTRAINT pk_product_id PRIMARY KEY (id);
```
