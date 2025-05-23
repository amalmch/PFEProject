import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { LoginComponent } from './login/login.component';
import { SignupComponent } from './signup/signup.component';
import { SidebarComponent } from './sidebar/sidebar.component';
import { MapType } from '@angular/compiler';

const routes: Routes = [
  {path: 'login',component:LoginComponent},
  {path: 'signup',component: SignupComponent},
  {path: 'sidebar',component: SidebarComponent},
 
  //{ path: '', redirectTo: '/login', pathMatch: 'full' } 
];

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule]
})
export class AppRoutingModule { }

