import { Component } from '@angular/core';
import { UserService } from '../services/user.service';
import { Router } from '@angular/router';
import { RoleService } from '../services/role-service.service';

@Component({
  selector: 'app-signup',
  templateUrl: './signup.component.html',
  styleUrls: ['./signup.component.css']
})
export class SignupComponent {
  firstName!: string;
  lastName!: string;
  email!: string;
  password!: string;
  confirmPassword!: string;
  phoneNumber!: string;
  selectedRole!: string;  // This will now hold the roleName (string)
  roles: any[] = [];  // Store role objects instead of just strings
  image!: File;
  imagePreview!: string;

  constructor(
    private roleService: RoleService,
    private userService: UserService,
    private router: Router
  ) {}

  ngOnInit(): void {
    this.loadRoles();
  }

  loadRoles(): void {
    this.roleService.getRoles().subscribe(
      (roles: string[]) => {  
        this.roles = roles;  
        console.log(this.roles);  // Log roles to confirm they're being fetched correctly
      },
      (error) => {
        console.error('Error fetching roles:', error);
      }
    );
  }
  
  onImageChange(event: Event): void {
    const fileInput = event.target as HTMLInputElement;
    if (fileInput?.files && fileInput.files[0]) {
      this.image = fileInput.files[0];
      const reader = new FileReader();
      reader.onload = () => {
        this.imagePreview = reader.result as string;
      };
      reader.readAsDataURL(this.image);
    }
  }

  onSubmit(): void {
    if (this.password !== this.confirmPassword) {
      alert("Passwords do not match.");
      return;
    }

    this.userService.signup(
      this.firstName,
      this.lastName,
      this.email,
      this.password,
      this.confirmPassword,
      this.phoneNumber,
      this.selectedRole,  // Use roleName from the selected role object
      this.image
    ).subscribe(
      (response) => {
        console.log(response);
        this.router.navigate(['/login']);
      },
      (error) => {
        console.error(error);
        alert('Error during signup');
      }
    );
  }
}
