require('dotenv').config();
const express = require('express');
const cors = require('cors');
const helmet = require('helmet');
const app = express();
const PORT = process.env.PAYMENTS_PORT || 3005;

app.use(helmet());
app.use(cors());
app.use(express.json());

app.get('/health', (req, res) => {
  res.json({ status: 'ok', service: 'payments-service', timestamp: new Date().toISOString() });
});

app.get('/api/payments/plans', (req, res) => {
  res.json({ plans: [{ id: 'free', name: 'Free Plan' }] });
});

app.listen(PORT, () => {
  console.log(`Payments Service running on port ${PORT}`);
});