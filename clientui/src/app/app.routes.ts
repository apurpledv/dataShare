import { Routes } from '@angular/router';
import { RegisterComponent } from './pages/register/register.component';
import { LoginComponent } from './pages/login/login.component';
import { AppComponent } from './app.component';
import { DashboardComponent } from './pages/dashboard/dashboard.component';

export const routes: Routes = [
    { path: '', component: AppComponent },
    { path: 'login', component: LoginComponent },
    { path: 'register', component: RegisterComponent },
    { path: 'dashboard', component: DashboardComponent }
];
