import { HttpClient } from '@angular/common/http';
import { Component } from '@angular/core';
import { Router } from '@angular/router';

@Component({
  selector: 'app-home-page',
  templateUrl: './home-page.component.html',
  styleUrls: ['./home-page.component.css'],
})
export class HomePageComponent {

  constructor(private http:HttpClient,private router:Router){}

  imageLoaded = false;

    // Navigation logic
    navigateTo(page: string): void {
      console.log(`Navigating to: ${page}`);
      this.router.navigate([`${page}`]); 
    }

  ngOnInit(): void {
    this.loadImage();
    if (window.location.pathname === '/') {
        this.checkSession();
    }
  }

  loadImage(): void {
    const img = new Image();
    img.src = '../../assets/ownerimg2.jpg';
    img.onload = () => {
      this.imageLoaded = true;
    };
  }

  checkSession(): void {
    try {
      this.http.get(
        'http://localhost:8080/api/status', { withCredentials: true }
      ).subscribe(
        (response: any) => {
          console.log("check session is called");
          console.log(response);
  
          // Ensure correct comparison based on the response format
          const isAuthenticated = response.isAuthenticated === 'true'; // Comparing as string
          const role = response.role ? response.role.toLowerCase() : ''; // Ensure role exists
  
          if (isAuthenticated) {
            if (role === 'owner') {
              this.router.navigate(['/owner-dashboard']);
            } else if (role === 'customer') {
              this.router.navigate(['/customer-dashboard']);
            } else if (role === 'agent') {
              this.router.navigate(['/agent-dashboard']);
            } else if (role === 'unknown') {
              this.router.navigate(['']);
            } else {
              console.warn('Unknown role:', role);
              this.router.navigate(['']);
            }
          } else {
            // Redirect to login page if not authenticated
            console.log("User is not authenticated");
            this.router.navigate(['/']);
          }
        },
        (error: any) => {
          // Handle specific error cases
          console.error('Error checking session:', error);
          // if (error.status === 0) {
          //   console.error("Network or CORS issue");
          // } else {
          //   console.error("HTTP Error:", error.status);
          // }
          this.router.navigate(['']);
        }
      );
    } catch (error: any) {
      console.error('Error checking session:', error);
      this.router.navigate(['']);
    }
  }


  login() {
    window.location.href = 'http://localhost:8080/oauth2/authorization/google';
  }

}