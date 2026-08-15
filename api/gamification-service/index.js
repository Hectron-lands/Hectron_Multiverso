require('dotenv').config();
const express = require('express');
const cors = require('cors');
const helmet = require('helmet');
const app = express();
const PORT = process.env.GAMIFICATION_PORT || 3006;

app.use(helmet());
app.use(cors());
app.use(express.json());

app.get('/health', (req, res) => {
  res.json({ status: 'ok', service: 'gamification-service', timestamp: new Date().toISOString() });
});

app.get('/api/gamification/achievements', (req, res) => {
  res.json({ achievements: [] });
});

app.listen(PORT, () => {
  console.log(`Gamification Service running on port ${PORT}`);
});