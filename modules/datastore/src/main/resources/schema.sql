CREATE TABLE IF NOT EXISTS search_keyword (
  id BIGINT NOT NULL AUTO_INCREMENT,
  keyword VARCHAR(20) NOT NULL UNIQUE,
  search_count BIGINT DEFAULT 0,
  version BIGINT NOT NULL DEFAULT 0,
  updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (id)
);

CREATE INDEX IF NOT EXISTS idx_search_count ON search_keyword (search_count DESC);
