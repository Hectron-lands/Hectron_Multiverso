import { BigQuery } from '@google-cloud/bigquery';
const bigquery = new BigQuery({ projectId: process.env.GCP_PROJECT_ID, location: 'US' });
export class BigQueryClient {
  async logEvent(eventType, data) {
    const dataset = bigquery.dataset(process.env.BIGQUERY_DATASET || 'hectron_prod');
    const table = dataset.table('events');
    await table.insert([{ event_type: eventType, data: JSON.stringify(data), timestamp: new Date().toISOString() }]);
  }
}