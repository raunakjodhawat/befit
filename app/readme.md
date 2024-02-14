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
2. `/api/v1/search/1` - Search by creator id
3. `/api/v1/create/user` - create a user
4. `/api/v1/create/food` - create a new entry for nutritional information
