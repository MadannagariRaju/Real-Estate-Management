import { HttpClient } from '@angular/common/http';
import { Router } from '@angular/router';
import { CanActivateFn, ActivatedRouteSnapshot } from '@angular/router';
import { inject } from '@angular/core';

interface RouteData {
  role?: string[];  
}

export const authGuard: CanActivateFn = async (route: ActivatedRouteSnapshot) => {
  const http = inject(HttpClient);
  const router = inject(Router);

  const AUTH_STATUS_URL = 'http://localhost:8080/api/status'; 

  try {
    const response = await http.get<any>(AUTH_STATUS_URL, { withCredentials: true }).toPromise();

    if (response.isAuthenticated) {
      const userRole = response.role; // Assume this is the role from the API
      const allowedRoles = (route.data as RouteData).role;   

      console.log(userRole)
      console.log(allowedRoles)
      console.log(typeof route.data)

      // Check if the user's role matches any of the allowed roles
      if (allowedRoles && allowedRoles.includes(userRole.toUpperCase())) {
        return true;  
      } else {
        console.log('Role mismatch. Redirecting...');
        await router.navigate([`${userRole}-dashboard`]);  
        return false;  // Deny access
      }
    } else {
      console.log('User not authenticated. Redirecting...');
      await router.navigate(['']);  
      return false;  // Deny access
    }
  } catch (error) {
    console.error('Error checking authentication status:', error);
    await router.navigate(['/']); 
    return false;  // Deny access
  }
};
