import { Component } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { HttpClient } from '@angular/common/http'; // Import HttpClient to call the API

@Component({
  selector: 'app-login',
  templateUrl: './login.component.html',
  styleUrls: ['./login.component.css']
})
export class LoginComponent {
  loginForm: FormGroup;

  constructor(private fb: FormBuilder, private http: HttpClient) {
    this.loginForm = this.fb.group({
      username: ['', [Validators.required, Validators.email]],  // Added email validator for username
      password: ['', [Validators.required, Validators.minLength(6)]],
    });
  }

  onSubmit(): void {
    if (this.loginForm.valid) {
      const loginData = this.loginForm.value;
      console.log('Login Data:', loginData);
      // Call your backend API to login here
      this.http.post('http://localhost:8082/api/users/login', loginData).subscribe(
        (response: any) => {
          console.log('Login response:', response);
          // Handle the response, save token or navigate
        },
        (error) => {
          console.error('Login error:', error);
          // Handle the error
        }
      );
    } else {
      console.log('Form is invalid');
    }
  }
}
