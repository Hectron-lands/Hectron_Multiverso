require('dotenv').config();
const express = require('express');
const cors = require('cors');
const helmet = require('helmet');
const app = express();
const PORT = process.env.UNIVERSE_PORT || 3002;

app.use(helmet());
app.use(cors());
app.use(express.json());

app.get('/health', (req, res) => {
  res.json({ status: 'ok', service: 'universe-service', timestamp: new Date().toISOString() });
});

app.post('/api/universe/create', (req, res) => {
  res.json({ universe: { id: 'test', name: req.body.name }, message: 'Universe created' });
});

app.listen(PORT, () => {
  console.log(`Universe Service running on port ${PORT}`);
});