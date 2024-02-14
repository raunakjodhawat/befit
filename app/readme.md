### APP
Main Backend application for the Nutritional Information API

## Endpoints
1. WebSocket: `/api/v1/ws/search` - Search for a food item using websocket
    - Request: 
        ```json
        {
            "query": "food item"
        }
        ```
2. GET: `/api/v1/search/:id` - Search by creator id
3. POST: `/api/v1/create/user` - create a user
4. POST: `/api/v1/create/food` - create a new entry for nutritional information
5. GET: `/api/v1/history/:id/:date` - get the history of a user
6. POST: `/api/v1/history` - add a new entry to the history of a user
7. PUT: `/api/v1/history` - update a users history