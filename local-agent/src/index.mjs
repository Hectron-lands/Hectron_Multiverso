import { WebSocket } from 'ws';
const OBS_HOST = process.env.OBS_HOST || '127.0.0.1';
const OBS_PORT = parseInt(process.env.OBS_PORT || '4455');
const OBS_PASSWORD = process.env.OBS_PASSWORD || '';

class OBSClient {
  constructor() { this.ws = null; }
  connect() {
    this.ws = new WebSocket(`ws://${OBS_HOST}:${OBS_PORT}`);
    this.ws.on('open', () => {
      console.log('Connected to OBS');
      if (OBS_PASSWORD) this.ws.send(JSON.stringify({ op: 1, d: { authentication: OBS_PASSWORD } }));
      this.ws.send(JSON.stringify({ op: 2, d: { name: 'HECTRON Agent' } }));
    });
  }
  setScene(scene) {
    if (this.ws && this.ws.readyState === WebSocket.OPEN) {
      this.ws.send(JSON.stringify({ op: 6, d: { name: scene } }));
    }
  }
}
const obs = new OBSClient();
obs.connect();
export default obs;