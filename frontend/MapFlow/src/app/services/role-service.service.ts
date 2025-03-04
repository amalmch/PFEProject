// role.service.ts
import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class RoleService {

  private apiUrl = 'http://localhost:8082/api/roles/get';  // API URL for fetching roles

  constructor(private http: HttpClient) { }

  getRoles(): Observable<string[]> {
    return this.http.get<string[]>(this.apiUrl);
  }
}
