import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Component } from '@angular/core';
import { Router } from '@angular/router';

@Component({
  selector: 'app-owner',
  templateUrl: './owner.component.html',
  styleUrls: ['./owner.component.css']
})
export class OwnerComponent {
  selectedFeature: string = ''; 
  name?:string

  constructor(private http: HttpClient,private router: Router) {
    
  }
  dropdownOpen = false;
  
  role = 'Owner';

  // Function to toggle the dropdown
  toggleDropdown() {
    this.dropdownOpen = !this.dropdownOpen;
  }



   // Navigation logic
   navigateTo(page: string): void {
    console.log(`Navigating to: ${page}`);
    this.selectedFeature = page; 
    this.router.navigate([`${page}`]);
  }
  ngOnInit(): void {
    this.getUserDetails();
  }


  
  getUserDetails(): void {
    const url = 'http://localhost:8080/api/username'; // Backend URL
    this.http.get(url, {
      headers: new HttpHeaders({
        'Content-Type': 'application/json',
      }),
      withCredentials: true, // Include credentials (cookies, session tokens, etc.)
    }).subscribe({
      next: (response: any) => {
        this.name = this.formatName(response.fullName);
        console.log('Name:', this.name);
        // return response;
      },
      error: (error: any) => {
        console.error('Error fetching properties:', error);
      }
    });
  }
  
   // Method to format the name
   formatName(name: string): string {
    if (!name) return name; 
    return name.charAt(0).toUpperCase() + name.slice(1).toLowerCase();
  }

  logout() {
    fetch('http://localhost:8080/api/logout', {
      method: 'POST',
      credentials: 'include',

      headers: {
        'Content-Type': 'application/json',
      },
    })
    .then((response) => {
      if (response.ok) {
        console.log('Logout successful');
        this.router.navigate(['/']);
      } else {
        console.error('Logout failed');
      }
    })
    .catch((error) => {
      console.error('Error during logout:', error);
    });
  }


  
}
