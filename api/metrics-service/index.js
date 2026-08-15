require('dotenv').config();
const express = require('express');
const cors = require('cors');
const helmet = require('helmet');
const app = express();
const PORT = process.env.METRICS_PORT || 3004;

app.use(helmet());
app.use(cors());
app.use(express.json());

app.get('/health', (req, res) => {
  res.json({ status: 'ok', service: 'metrics-service', timestamp: new Date().toISOString() });
});

app.get('/api/metrics', (req, res) => {
  res.json({ metrics: { users: 0, universes: 0, planets: 0 } });
});

app.listen(PORT, () => {
  console.log(`Metrics Service running on port ${PORT}`);
});