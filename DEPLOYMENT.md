# Deployment & GitHub Guide — Student Management System

This guide walks you through pushing your project to GitHub and deploying the application using Docker, Render, Railway, or Vercel.

---

## Part 1: Push to GitHub

Your local git repository has been initialized with `main` and your first commit is ready.

### Step 1: Create a new repository on GitHub
1. Go to [https://github.com/new](https://github.com/new).
2. Enter a repository name, e.g.: **`student-management-system`**.
3. Choose **Public** (or **Private**).
4. **Leave all checkboxes UNCHECKED** (do not initialize with README, .gitignore, or license, as we already have them).
5. Click **Create repository**.

### Step 2: Link and Push from your terminal
Copy your repository URL from GitHub and run these commands in `D:\01_PROJECTS\STUDENT-MANAGEMENT`:

```bash
git remote add origin https://github.com/<YOUR-USERNAME>/student-management-system.git
git push -u origin main
```

*(Git will prompt for your GitHub credentials or Personal Access Token if not already logged in).*

> **Security Note**: Your `.env` files containing `DB_PASSWORD=Harsh123@` and JWT secrets are strictly protected by `.gitignore` and will **never** be uploaded to GitHub. Only safe `.env.example` templates are pushed.

---

## Part 2: Deployment Options

### Option A: Local / VPS Single-Command Deployment (Docker Compose)
We have configured a production multi-stage [Dockerfile](file:///d:/01_PROJECTS/STUDENT-MANAGEMENT/backend/Dockerfile) for backend, [Dockerfile](file:///d:/01_PROJECTS/STUDENT-MANAGEMENT/frontend/Dockerfile) + [nginx.conf](file:///d:/01_PROJECTS/STUDENT-MANAGEMENT/frontend/nginx.conf) for frontend, and [docker-compose.yml](file:///d:/01_PROJECTS/STUDENT-MANAGEMENT/docker-compose.yml).

To spin up MySQL 8, Spring Boot 3, and React 18 in one command:

```bash
docker compose up -d --build
```

- **Frontend App**: `http://localhost` (Port 80)
- **Backend API**: `http://localhost:8080/api`
- **MySQL Database**: `localhost:3306`

To shut down:
```bash
docker compose down
```

---

### Option B: Deploy to Render.com (Recommended for Free Cloud Hosting)

Render offers free hosting for web services and static sites:

#### 1. Database (Render MySQL / PostgreSQL or Aiven MySQL)
1. Go to [Render Dashboard](https://dashboard.render.com/) or [Aiven.io](https://aiven.io/).
2. Create a free MySQL database and copy the JDBC connection string (`Host`, `Port`, `Database`, `User`, `Password`).

#### 2. Backend Service (Spring Boot)
1. In Render, click **New +** $\to$ **Web Service**.
2. Connect your GitHub repository.
3. Choose:
   - **Root Directory**: `backend`
   - **Runtime**: `Docker` (Render will automatically detect `backend/Dockerfile`)
   - **Environment Variables**:
     ```text
     DB_HOST=<your-cloud-db-host>
     DB_PORT=3306
     DB_USERNAME=<your-db-user>
     DB_PASSWORD=<your-db-password>
     JWT_SECRET=404E635266556A586E3272357538782F413F4428472B4B6250645367566B5970
     CORS_ORIGIN=https://<your-frontend-domain>.onrender.com
     ```
4. Click **Create Web Service**. Your backend will deploy to `https://sms-backend.onrender.com`.

#### 3. Frontend Service (React + Vite)
1. In Render, click **New +** $\to$ **Static Site**.
2. Connect your GitHub repository.
3. Configure:
   - **Root Directory**: `frontend`
   - **Build Command**: `npm install && npm run build`
   - **Publish Directory**: `dist`
   - **Environment Variables**:
     ```text
     VITE_API_URL=https://sms-backend.onrender.com/api
     ```
4. Click **Create Static Site**.

---

### Option C: Deploy to Railway.app

1. Go to [Railway.app](https://railway.app/) and sign in with GitHub.
2. Click **New Project** $\to$ **Provision MySQL**.
3. Click **Add Service** $\to$ **GitHub Repo** $\to$ Select `student-management-system`.
   - Set Root Directory to `backend`.
   - Railway builds the `Dockerfile` automatically.
   - Link environment variables: `DB_HOST=${{MySQL.MYSQLHOST}}`, `DB_PASSWORD=${{MySQL.MYSQLPASSWORD}}`, etc.
4. Add another service for `frontend`:
   - Set Root Directory to `frontend`.
   - Set `VITE_API_URL` to the backend's generated Railway domain.

---

### Option D: Deploy Frontend to Vercel

If you deploy your backend on Render/Railway/AWS, you can deploy the React frontend to Vercel in 30 seconds:

1. Go to [Vercel](https://vercel.com/) and click **Add New Project**.
2. Import your GitHub repository.
3. Set **Root Directory** to `frontend`.
4. Add environment variable:
   - `VITE_API_URL`: `https://your-deployed-backend-url/api`
5. Click **Deploy**. Vercel will build and serve your React app on a global CDN edge.
