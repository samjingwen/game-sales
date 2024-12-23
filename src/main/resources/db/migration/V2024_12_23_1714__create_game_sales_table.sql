CREATE TABLE game_sales
(
    id           BIGINT AUTO_INCREMENT PRIMARY KEY,
    game_no      INT           NOT NULL,
    game_name    VARCHAR(20)   NOT NULL,
    game_code    VARCHAR(5)    NOT NULL,
    type         INT           NOT NULL,
    cost_price   DECIMAL(5, 2) NOT NULL,
    tax          DECIMAL(5, 2) NOT NULL,
    sale_price   DECIMAL(5, 2) NOT NULL,
    date_of_sale DATE          NOT NULL,
    INDEX        idx_date_of_sale (date_of_sale),
    INDEX        idx_sale_price (sale_price)
);

