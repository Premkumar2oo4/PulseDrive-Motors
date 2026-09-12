# PulseDrive Motors deployment

The project has two separately deployed applications:

- `frontend`: React/Vite, deployed to Vercel
- `backend`: Spring Boot, deployed to a Java/Docker host such as Render or Railway

## 1. Deploy the backend

Create a web service from this repository and set its root directory to `backend`.
Use the included `Dockerfile`. Set the health-check path to `/api/health`.

Add the variables listed in `backend/.env.example` to the backend host. Do not
commit real passwords or API secrets. The important values are the hosted MySQL
connection, `JWT_SECRET`, and later `CORS_ALLOWED_ORIGINS`.

When deployment finishes, copy the backend URL. If the host gives you:

`https://pulsedrive-api.example.com`

then the frontend API value is:

`https://pulsedrive-api.example.com/api`

Confirm the backend is running by opening:

`https://pulsedrive-api.example.com/api/health`

## 2. Deploy the frontend to Vercel

Import the same repository into Vercel and use these settings:

- Root Directory: `frontend`
- Framework Preset: Vite
- Build Command: `npm run build`
- Output Directory: `dist`

Add this Vercel environment variable for Production, Preview, and Development:

`VITE_API_URL=https://pulsedrive-api.example.com/api`

Deploy the project. Copy the resulting Vercel URL, for example:

`https://pulsedrive-motors.vercel.app`

## 3. Allow the Vercel frontend in the backend

On the backend host, set:

`CORS_ALLOWED_ORIGINS=https://pulsedrive-motors.vercel.app`

Use comma-separated URLs if more than one frontend must be allowed:

`CORS_ALLOWED_ORIGINS=http://localhost:5173,https://pulsedrive-motors.vercel.app`

Redeploy/restart the backend after changing the variable.

## 4. Final checks

1. Open the Vercel site and confirm vehicles load.
2. Register and sign in.
3. Confirm the browser Network tab calls the hosted API, not localhost.
4. Test admin pages, images, email, and Razorpay only after their secrets are set.

## Security note

The originally uploaded configuration contained literal credential values.
This deployment copy uses environment variables instead. Rotate any database,
JWT, Razorpay, email, or Cloudinary secret that has previously been committed or
shared.
