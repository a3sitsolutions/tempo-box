-- Adiciona coluna id_token na tabela file_metadata
ALTER TABLE file_metadata ADD COLUMN id_token VARCHAR(255);

-- Cria índice para melhorar performance nas consultas por auth_token e id_token
CREATE INDEX idx_file_metadata_auth_token_id_token ON file_metadata(auth_token, id_token);