CREATE DATABASE IF NOT EXISTS confeitaria_db;
USE confeitaria_db;

-- 1. Tabela de Clientes
CREATE TABLE IF NOT EXISTS clientes (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    nome VARCHAR(100) NOT NULL,
    telefone VARCHAR(20) NOT NULL,
    endereco TEXT NOT NULL
);

-- 2. Tabela de Produtos
CREATE TABLE IF NOT EXISTS produtos (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    nome VARCHAR(100) NOT NULL,
    preco_unitario DECIMAL(10,2) NOT NULL,
    categoria ENUM('SALGADOS_FRITOS', 'SALGADOS_ASSADOS', 'DOCES_FINOS', 'DOCES_SIMPLES') NOT NULL
);

-- 3. Tabela de Pedidos (Atualizada com tipo_entrega)
CREATE TABLE IF NOT EXISTS pedidos (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    cliente_id BIGINT,
    data_encomenda DATE NOT NULL,
    horario_encomenda TIME NOT NULL,
    valor_total DECIMAL(10,2) NOT NULL,
    status VARCHAR(20) DEFAULT 'PENDENTE',
    tipo_entrega VARCHAR(20) DEFAULT 'ENTREGA', -- 🌟 NOVA COLUNA INTEGRADA
    data_criacao TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (cliente_id) REFERENCES clientes(id)
);

-- 4. Tabela de Itens do Pedido
CREATE TABLE IF NOT EXISTS itens_pedido (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    pedido_id BIGINT,
    produto_id BIGINT,
    quantidade INT NOT NULL,
    preco_praticado DECIMAL(10,2) NOT NULL,
    sabor VARCHAR(50) DEFAULT NULL,
    FOREIGN KEY (pedido_id) REFERENCES pedidos(id),
    FOREIGN KEY (produto_id) REFERENCES produtos(id)
);