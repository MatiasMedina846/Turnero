interface AppConfig {
  apiUrl?: string;
}

/**
 * Base URL of the backend API. It is read at runtime from `config.js`
 * (see public/config.js), so the deployed site can point to any backend
 * without rebuilding. Empty string = same origin (dev server proxy).
 */
const cfg = (globalThis as { APP_CONFIG?: AppConfig }).APP_CONFIG;

export const API_BASE = (cfg?.apiUrl ?? '').replace(/\/$/, '');
