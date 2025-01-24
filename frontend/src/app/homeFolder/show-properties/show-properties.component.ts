import { HttpHeaders } from '@angular/common/http';
import { HttpClient } from '@angular/common/http';
import { Component } from '@angular/core';
import { Router } from '@angular/router';

@Component({
  selector: 'app-show-properties',
  templateUrl: './show-properties.component.html',
  styleUrls: ['./show-properties.component.css']
})
export class ShowPropertiesComponent {
  searchQuery: string = '';
  properties: any[] = []; 
  filteredProperties: any[] = [];


  ownerDetails: any | null = null;
  agentDetails: any | null = null;

  selectedPropertyId: number | null = null;

  messageBoxVisible: boolean = false;
  messageContent: string = '';

  messageBoxVisible1: boolean = false;

  

  filters = {
    minPrice: null as number | null,
    maxPrice: null as number | null,
    propertyType: '',
    amenities: ''
  };

  constructor(private http: HttpClient , private router: Router) {}

  isImageModalOpen: boolean = false;
  currentImageUrl: string | null = null;

  private viewedProperties = new Set<number>(); 


  openImageModal(imageUrl: string, propertyId: number): void {
    console.log(propertyId);
    this.currentImageUrl = imageUrl;
    this.isImageModalOpen = true;

    // Increment view count only if not already done for this property
    if (!this.viewedProperties.has(propertyId)) {
      this.incrementViewCount(propertyId);
      this.viewedProperties.add(propertyId);
    }
  }


  incrementViewCount(propertyId: number): void {
    const url = `http://localhost:8080/analytics/incrementViews?propertyId=${propertyId}`;
    this.http.put(url, {}, { withCredentials: true }).subscribe({
      next: () => {
        console.log(`View count incremented for property ${propertyId}`);
      },
      error: (error: any) => {
        console.error('Error incrementing view count:', error);
      }
    });
  }
  
  

  closeImageModal(): void {
    this.isImageModalOpen = false;
    this.currentImageUrl = null;
  }


  ngOnInit(): void {
    this.fetchAllProperties(); 
  }

  navigateTo(route: string): void {
    this.router.navigate([`/${route}`]);
  }

  fetchAllProperties(): void {
    const url = 'http://localhost:8080/prop/getallproperties'; // Backend URL
    this.http.get(url, {
      headers: new HttpHeaders({
        'Content-Type': 'application/json',
      }),
      withCredentials: true, // Include credentials (cookies, session tokens, etc.)
    }).subscribe({
      next: (response: any) => {
        console.log('Response from backend:', response);
        this.properties = response.map((property: any) => {
          property.imageUrl = `http://localhost:8080/prop/getPropertyImage/${property.id}`;
          property.videoUrl = `http://localhost:8080/prop/getPropertyVideo/${property.id}`;
          property.floorPlanUrl = `http://localhost:8080/prop/getPropertyFloorPlan/${property.id}`;
          console.log('Property Floor Plan URL:', property.floorPlanUrl);

          return property;
        });
        console.log('Properties fetched:', this.properties);
        this.filteredProperties = [...this.properties];
        console.log("filtered Properties",this.filteredProperties)
      },
      error: (error: any) => {
        console.error('Error fetching properties:', error);
      }
    });
  }

  searchProperties(): void {
    this.applyFilters();
  }

  // Filter properties based on the search query

  applyFilters(): void {
    this.filteredProperties = this.properties.filter((property) => {
      const matchesPrice =
        (!this.filters.minPrice || property.price >= this.filters.minPrice) &&
        (!this.filters.maxPrice || property.price <= this.filters.maxPrice);
  
      const matchesType =
        !this.filters.propertyType || property.propertyType === this.filters.propertyType;
  
      const matchesSearch =
        this.searchQuery.trim() === '' ||
        property.title.toLowerCase().includes(this.searchQuery.toLowerCase()) ||
        property.description.toLowerCase().includes(this.searchQuery.toLowerCase()) ||
        property.location.toLowerCase().includes(this.searchQuery.toLowerCase());

        const amenitiesArray = property.amenities && typeof property.amenities === 'string'
        ? property.amenities.split(',').map((amenity: string) => amenity.toLowerCase().trim())
        : [];

        console.log(' Amenities Array:',amenitiesArray);

        const matchesAmenities = (
        !this.filters.amenities || amenitiesArray.includes(this.filters.amenities.toLowerCase())
        );

        console.log('Filter Amenities:', this.filters.amenities);
        
        console.log("matchesAmenities",matchesAmenities)

        return matchesPrice && matchesType && matchesSearch && matchesAmenities;
  
    });
  
    console.log('Filtered Properties:', this.filteredProperties);  // Debugging line
  }



  
  login() {
    window.location.href = 'http://localhost:8080/oauth2/authorization/google';
  }


  
}
