import { Component } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { HttpClient } from '@angular/common/http';

@Component({
  selector: 'app-role-selection',
  templateUrl: './role-selection.component.html',
  styleUrls: ['./role-selection.component.css'],
})
export class RoleSelectionComponent {
  email: string | undefined;
  name: string | undefined;


  constructor(private route: ActivatedRoute, private http: HttpClient, private router: Router) {}

  ngOnInit() {
    this.route.queryParams.subscribe((params: { [x: string]: string | undefined; }) => {
      this.email = params['email'];
      this.name = params['name'];
    });
 }
  selectRole(role: string) {
    this.http.post('http://localhost:8080/api/save-role', { email: this.email, role }, { responseType: 'text' })
      .subscribe({
        next: (response: string) => {
          console.log('Role saved:', response);
          this.router.navigate([`/${response}-dashboard`]); // Navigate based on the role
        },
        error: (error: any) => {
          console.error('Error saving role:', error);
        }
      });
  }


  }

