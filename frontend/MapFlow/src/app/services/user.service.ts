import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { catchError, Observable, tap, throwError } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class UserService {
  private apiUrl = 'http://localhost:8082/api/users/add';  // Adjust URL to match your backend

  constructor(private http: HttpClient) { }

  // Signup method that accepts individual arguments
  signup(firstName: string, lastName: string, email: string, password: string, confirmPassword: string, phoneNumber: string, role: string, image?: File): Observable<any> {
    const formData: FormData = new FormData();
    formData.append("firstName", firstName);
    formData.append("lastName", lastName);
    formData.append("email", email);
    formData.append("password", password);
    formData.append("confirmPassword", confirmPassword);
    formData.append("phoneNumber", phoneNumber);
    formData.append("role", role);
    if (image) {
      formData.append("image", image, image.name);
    }

    return this.http.post<any>(this.apiUrl, formData).pipe(
      tap(response => {
        if (response.token) {
          const token = response.token;
          const userId = response.user.id;
          const username = response.user.username;

          // Store the token and user information in localStorage
          localStorage.setItem('authToken', token);
          localStorage.setItem('userId', userId);
          localStorage.setItem('username', username);

          console.log('Stored Auth Token:', token);
          console.log('Stored User ID:', userId);
          console.log('Stored Username:', username);
        } else {
          console.error('Signup response is undefined or missing token');
        }
      }),
      catchError(error => {
        console.error('Signup error:', error);
        return throwError(() => new Error('Signup failed'));
      })
    );
  }
}
