import { HttpClient } from '@angular/common/http';
import { Component } from '@angular/core';
import { Router } from '@angular/router';
import { NavbarComponent } from "../navbar/navbar.component";

@Component({
  selector: 'app-contacted',
  templateUrl: './contacted.component.html',
  styleUrls: ['./contacted.component.css'],
})
export class ContactedComponent {

  constructor(private http:HttpClient, private router:Router){}

  ngOnInit():void{
    this.getContactedDetails();
  }

  data: any =[];
  navigateTo(route: string): void {
    this.router.navigate([`/${route}`]);
  }


  getContactedDetails() {
    this.http.get('http://localhost:8080/messages/get-contacted-details',{withCredentials:true})
      .subscribe(
        (response: any) => {
          this.data = response.properties
          
          console.log("data",this.data)
          // Handle the successful response here
          console.log('Contacted Details:', response);
        },
        (error: any) => {
          // Handle the error here
          console.error('Error fetching contacted details:', error);
        }
      );
  }
  

  logout() {

    console.log("customer logout")

    fetch('http://localhost:8080/api/logout', {
      method: 'POST',
      credentials: 'include',
      headers: {
        'Content-Type': 'application/json'
      }
    })
      .then(response => {
        if (response.ok) {
          console.log('Logout successful');
          this.router.navigate(['/']);
        } else {
          console.error('Logout failed');
        }
      })
      .catch(error => {
        console.error('Error during logout:', error);
      });
  }

}
