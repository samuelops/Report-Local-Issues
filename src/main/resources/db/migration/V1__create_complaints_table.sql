CREATE TABLE complaints (
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  tracking_id VARCHAR(64) NOT NULL UNIQUE,
  name VARCHAR(100) NOT NULL,
  email VARCHAR(120),
  phone VARCHAR(20),
  title VARCHAR(200) NOT NULL,
  description TEXT NOT NULL,
  image_path VARCHAR(500),
  status VARCHAR(20) NOT NULL DEFAULT 'SUBMITTED',
  latitude DECIMAL(10,6),
  longitude DECIMAL(10,6),
  address VARCHAR(400),
  admin_notes TEXT,
  created_at DATETIME NOT NULL,
  updated_at DATETIME,
  INDEX idx_tracking_id (tracking_id)
);