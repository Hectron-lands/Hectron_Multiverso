require('dotenv').config();
const express = require('express');
const cors = require('cors');
const helmet = require('helmet');
const app = express();
const PORT = process.env.AUTONOMY_PORT || 3001;

app.use(helmet());
app.use(cors());
app.use(express.json());

app.get('/health', (req, res) => {
  res.json({ status: 'ok', service: 'autonomy-service', timestamp: new Date().toISOString() });
});

app.get('/api/autonomy/status', (req, res) => {
  res.json({ autonomy: { enabled: true, currentScene: 'DEFAULT' } });
});

app.listen(PORT, () => {
  console.log(`Autonomy Service running on port ${PORT}`);
});