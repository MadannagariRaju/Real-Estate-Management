import { HttpHeaders } from '@angular/common/http';
import { HttpClient } from '@angular/common/http';
import { OnInit } from '@angular/core';
import { Component } from '@angular/core';
import { Router } from '@angular/router';
import { ChangeDetectorRef } from '@angular/core';
import { ToastrService } from 'ngx-toastr';
import { NavbarComponent } from "../navbar/navbar.component";


@Component({
  selector: 'app-customer',
  templateUrl: './customer.component.html',
  styleUrls: ['./customer.component.css'],
  // imports: [NavbarComponent],
})
export class CustomerComponent {
  searchQuery: string = '';
  properties: any[] = [];
  filteredProperties: any[] = [];
  isMapVisible = false;
  data: any[] =[]
  google: any;



  ownerDetails: any | null = null;
  agentDetails: any | null = null;

  selectedPropertyId: number | null = null;

  messageBoxVisible: boolean = false;
  messageContent: string = '';

  messageBoxVisible1: boolean = true;

  contactedProperties = new Set<number>(); // Set to store contacted property ids


  filters = {
    minPrice: null as number | null,
    maxPrice: null as number | null,
    propertyType: '',
    amenities: ''
  };

  constructor(private http: HttpClient, private router: Router, private cdr: ChangeDetectorRef, private toastr: ToastrService) { }

  isImageModalOpen: boolean = false;
  currentImageUrl: string | null = null;

  private viewedProperties = new Set<number>(); // Track properties with incremented views


  toggleMap() {
    this.isMapVisible = !this.isMapVisible;
    if (this.isMapVisible) {
      setTimeout(() => {
        this.loadMap(); // Delay to ensure DOM element is rendered
      }, 0);
    } else {
      this.fetchAllProperties();
    }
  }

  openImageModal(imageUrl: string, propertyId: number): void {
    console.log(propertyId);
    this.currentImageUrl = imageUrl;
    this.isImageModalOpen = true;
    this.cdr.detectChanges();
    if (!this.viewedProperties.has(propertyId)) {
      this.incrementViewCount(propertyId);
      this.viewedProperties.add(propertyId);
    }
  }



  incrementViewCount(propertyId: number): void {
    const url = `http://localhost:8080/analytics/incrementViews?propertyId=${propertyId}`;
    this.http.put(url, {}, { withCredentials: true }).subscribe({
      next: () => {
        this.cdr.detectChanges();
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
    this.cdr.detectChanges();
  }
isloaded:boolean=false

  ngOnInit(): void {
    this.fetchAllProperties();
    this.getContactedDetails();
    this.loadMap();
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
          // console.log('Property Floor Plan URL:', property.floorPlanUrl);

          return property;
        });
        // console.log('Properties fetched:', this.properties);
        this.filteredProperties = [...this.properties];
        this.cdr.detectChanges();
        console.log("filtered Properties search bar", this.filteredProperties)
      },
      error: (error: any) => {
        console.error('Error fetching properties:', error);
      }
    });
  }



  searchProperties(): void {
    this.applyFilters();
  }




  applyFilters(): void {
    this.filteredProperties = this.properties.filter((property) => {
      const matchesPrice =
        (!this.filters.minPrice || property.price >= this.filters.minPrice) &&
        (!this.filters.maxPrice || property.price <= this.filters.maxPrice);

      const matchesType =
        !this.filters.propertyType || property.propertyType === this.filters.propertyType;

      const matchesSearch =
        this.searchQuery.trim() === '' ||
      property.location.toLowerCase().includes(this.searchQuery.toLowerCase());

      const amenitiesArray = property.amenities && typeof property.amenities === 'string'
        ? property.amenities.split(',').map((amenity: string) => amenity.toLowerCase().trim())
        : [];

      // console.log(' Amenities Array:', amenitiesArray);

      const matchesAmenities = (
        !this.filters.amenities || amenitiesArray.includes(this.filters.amenities.toLowerCase())
      );

      console.log('Filter Amenities:', this.filters.amenities);
      // this.filteredProperties = []

      console.log("matchesSearch",matchesSearch)

      // console.log("matchesAmenities", matchesAmenities)
      this.cdr.detectChanges();

      return matchesPrice && matchesType && matchesSearch && matchesAmenities;

    });

    console.log('Filtered Properties:', this.filteredProperties);  // Debugging line
  }


  viewOwnerDetails(propertyId: number): void {
    this.cdr.detectChanges();

    if (this.selectedPropertyId === propertyId) {
      this.messageBoxVisible1 = !this.messageBoxVisible1;
    }



    const url = `http://localhost:8080/profile/getownerbyproperty/${propertyId}`;
    this.http.get<any>(url).subscribe({
      next: (data: any) => {
        console.log(data);
        this.selectedPropertyId = propertyId;

        if (data.role === 'owner') {
          // Handle owner details
          this.ownerDetails = data.ownerDetails;
          this.agentDetails = null;
          console.log('Owner Details:', this.ownerDetails);
        } else if (data.role === 'agent') {
          console.log("agent details ......")
          // Handle agent details
          this.agentDetails = {
            name: data.name,
            bussinessName: data.businessName,
            email: data.email,
            phone: data.phno,

          };
          this.ownerDetails = null;
        }

        this.cdr.detectChanges(); // Ensure UI updates
      },
      error: (error: any) => {
        console.error('Error fetching owner details:', error);
      }
    });
  }


  toggleMessageBox(propertyId: number): void {
    console.log(propertyId)
    console.log(this.selectedPropertyId)
    this.cdr.detectChanges();
    if (this.selectedPropertyId === propertyId) {
      this.messageBoxVisible = !this.messageBoxVisible;
      console.log(this.messageBoxVisible)
    } else {
      this.messageBoxVisible = false
    }
  }

  sendMessageToOwner(propertyId: number): void {

    if (!this.contactedProperties.has(propertyId)) {
      // Mark as contacted in the set
      this.contactedProperties.add(propertyId);
    }

    if (this.messageContent.trim() === '') {
      alert('Please write a message before sending.');
      return;
    }

    const messagePayload = {
      propertyId: propertyId,
      message: this.messageContent,
    };

    const url = 'http://localhost:8080/messages/send';

    console.log(messagePayload);

    this.http.post(url, messagePayload, { withCredentials: true }).subscribe({
      next: () => {
        console.log(`Message sent to owner of property ${propertyId}.`);
        this.messageContent = '';
        this.messageBoxVisible = false;
        // alert('Message sent successfully!');
        this.toastr.success('Message sent successfully!', 'Success', {
          timeOut: 5000,
          progressBar: true
        });
          // Add the property id to the set to mark it as contacted
        this.contactedProperties.add(propertyId);
      this.messageBoxVisible1 = !this.messageBoxVisible1


        const audio = new Audio('/assets/ting-sound.mp3');
        audio.play();
        this.cdr.detectChanges();

      },
      error: (error: any) => {
        this.toastr.error('unable to send message!', 'Error');
        console.error('Error sending message:', error);
        // alert('Failed to send the message. Please try again.');
      },
    });

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

  propID?: []

  loadMap() {
    this.filteredProperties = [];
    this.properties = []
    const map = new google.maps.Map(document.getElementById("google-map") as HTMLElement, {
      center: { lat: 17.385044, lng: 78.486671 },
      zoom: 10
    });
      // Call the function to add markers to the map
      this.addMarkersToMap(map);

    // Add click event to get latitude and longitude
    google.maps.event.addListener(map, 'click', (event: any) => {
      this.filteredProperties = []
      const lat = event.latLng.lat();
      const lng = event.latLng.lng();
      // alert(`Latitude: ${lat}, Longitude: ${lng}`);

      // Sending latitude and longitude to the backend
      const geoLocation = {
        latitude: lat,
        longitude: lng,
      };

      // Constructing the URL with query parameters
      const url = `http://localhost:8080/geolocation/get-properties-onpoints?latitude=${geoLocation.latitude}&longitude=${geoLocation.longitude}`;

      this.http.get(url, { withCredentials: true }).subscribe({
        next: (response: any) => {
          console.log('Response from backend:', response.Properties);

          // Check if response contains the GeoLocationsFound field
          if (response && response.GeoLocationsFound) {
            this.propID = response.Properties;
            console.log(this.propID);

            // Now map the properties and set additional URLs for images, videos, etc.
            this.properties = [];

            // Create an array of promises to load images, videos, and floor plans
            const promises: any[] = [];

            // Iterate through each array inside Properties array
            response.Properties.forEach((propertyArray: any[]) => {
              propertyArray.forEach((property: any) => {
                const imagePromise = this.http.get(`http://localhost:8080/prop/getPropertyImage/${property.id}`, { responseType: 'blob', withCredentials: true })
                  .toPromise()
                  .then((imageData: Blob | undefined) => {
                    if (imageData) {
                      const imageUrl = URL.createObjectURL(imageData);
                      property.imageUrl = imageUrl; // Set the imageUrl once the data is fetched
                      console.log("imageUrl", imageUrl);
                    }
                  });

                const videoPromise = this.http.get(`http://localhost:8080/prop/getPropertyVideo/${property.id}`, { responseType: 'blob', withCredentials: true })
                  .toPromise()
                  .then((videoData: Blob | undefined) => {
                    if (videoData) {
                      const videoUrl = URL.createObjectURL(videoData);
                      property.videoUrl = videoUrl; // Set the videoUrl
                      console.log("videoUrl", videoUrl);
                    }
                  });

                const floorPlanPromise = this.http.get(`http://localhost:8080/prop/getPropertyFloorPlan/${property.id}`, { responseType: 'blob', withCredentials: true })
                  .toPromise()
                  .then((floorPlanData: Blob | undefined) => {
                    if (floorPlanData) {
                      const floorPlanUrl = URL.createObjectURL(floorPlanData);
                      property.floorPlanUrl = floorPlanUrl; // Set the floorPlanUrl
                      console.log("floorPlanUrl", floorPlanUrl);
                    }
                  });

                promises.push(Promise.all([imagePromise, videoPromise, floorPlanPromise]));

                promises.push(
                  Promise.all(promises).then(() => {
                    this.properties.push(property);
                    console.log("this.properties are", this.properties)
                    this.filteredProperties = [...this.properties];
                    this.applyFilters();
                    this.cdr.detectChanges();
                    console.log("this.filteredProperties in map", this.filteredProperties);
                  }),
                );
              });
            });
          }
          this.toastr.success('Properties fetched successfully!', 'Success', {
            timeOut: 5000,
            progressBar: true
          });

        },
        error: (error: any) => {
          console.error('Error fetching properties:', error);
          // alert('Failed to fetch properties.');
          this.toastr.warning('No Property Found!', 'Error', {
            timeOut: 5000,
            progressBar: true
          });
        }
      });
    });
  }

  


  addMarkersToMap(map: google.maps.Map) {
    // Fetch the geo-locations
    this.http.get<any[]>('http://localhost:8080/geolocation/get-all-geolocations', { withCredentials: true }).subscribe({
      next: (response: any[]) => {
        console.log('GeoLocations from backend:', response);
  
        response.forEach((geoLocation: any) => {
          const latitude = geoLocation.latitude;
          const longitude = geoLocation.longitude;
  
          const marker = new google.maps.Marker({
            position: { lat: latitude, lng: longitude },
            map: map,
            title: geoLocation.property.propertyType,
            icon: {
              url: geoLocation.property?.propertyType === 'rent'
                ? 'assets/blue.svg'  // Path to local SVG for rent
                : 'assets/red.svg',  // Path to local SVG for sale
              scaledSize: new google.maps.Size(40, 40),  // Adjust size
              anchor: new google.maps.Point(16, 32)
            }
          });
          
          

          // Create the InfoWindow content
          const infoWindowContent = `
            <h5>Property</h5>
            <p><strong>Title:</strong> ${geoLocation.property?.title}</p>
            <p><strong>Type:</strong> ${geoLocation.property?.propertyType}</p>
            <p><strong>Contact:</strong> ${geoLocation.property?.contact}</p>
            
          `;
  
          const infoWindow = new google.maps.InfoWindow({
            content: infoWindowContent,
          });
  
          // Add click event to open InfoWindow
          marker.addListener('click', () => {
            infoWindow.open(map, marker);
          });
        });
  
        this.toastr.success('Geo-locations fetched successfully!', 'Success', {
          timeOut: 5000,
          progressBar: true,
        });
      },
      error: (error: any) => {
        console.error('Error fetching geo-locations:', error);
        this.toastr.warning('Failed to fetch geo-locations.', 'Error', {
          timeOut: 5000,
          progressBar: true,
        });
      }
    });
}

  

getContactedDetails() {
  this.http.get('http://localhost:8080/messages/get-contacted-details', { withCredentials: true })
    .subscribe(
      (response: any) => {
        console.log('Full API Response:', response.properties);

        if (response && Array.isArray(response.properties)) {
          // Correctly return the propertyId inside the map function
          const contactedIds = response.properties.map((property: any) => {
            console.log("inside map : ", property.propertyId);  // Log the propertyId
            return property.propertyId;  // Ensure to return the propertyId
          });
          console.log('contactedIds:', contactedIds);  // Log the extracted IDs
          
          // Set the contacted properties
          this.contactedProperties = new Set(contactedIds);
          console.log('contactedProperties Set:', this.contactedProperties);
        } else {
          console.error('Invalid structure: properties is missing or not an array');
        }
      },
      (error: any) => {
        console.error('Error fetching contacted details:', error);
      }
    );
}



}
