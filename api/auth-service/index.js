require('dotenv').config();
const express = require('express');
const cors = require('cors');
const helmet = require('helmet');
const jwt = require('jsonwebtoken');
const app = express();
const PORT = process.env.AUTH_PORT || 3003;

app.use(helmet());
app.use(cors());
app.use(express.json());

const JWT_SECRET = process.env.JWT_SECRET || 'default_secret';

app.get('/health', (req, res) => {
  res.json({ status: 'ok', service: 'auth-service', timestamp: new Date().toISOString() });
});

app.post('/api/auth/login', (req, res) => {
  res.json({ token: 'test_token', message: 'Login endpoint' });
});

app.post('/api/auth/signup', (req, res) => {
  res.json({ token: 'test_token', message: 'Signup endpoint' });
});

app.listen(PORT, () => {
  console.log(`Auth Service running on port ${PORT}`);
});