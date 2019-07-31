# Approach for Booking and availability.

**Background**
- Pre registration has booking module where citizen could book the time slot for a given registration center.

The target users are -
   - Citizen

The key requirements are -
-   Able to search registration center with multiple criteria.
-  Get the availability of the registration center for a given date.
- Reserving a time slot.

NFR-
1. availability should be consistently updated and viewed.
2. NFR for the response time should be 3 seconds.
3. availability server side transaction should be 200ms.

 

**Solution**

**component Diagram**
![booking-component-diagram](_images/preregd_booking_component.png)
