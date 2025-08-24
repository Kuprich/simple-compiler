<<<<<<< HEAD
<<<<<<< HEAD
=======
import './assets/main.css'

>>>>>>> dbc6549 (renamed backend project folder and created vue started template)
=======
>>>>>>> 8823be2 (fix: update application.yml for Docker backend - Vue frontend not displaying correctly yet)
import { createApp } from 'vue'
import { createPinia } from 'pinia'

import App from './App.vue'
import router from './router'

const app = createApp(App)

app.use(createPinia())
app.use(router)

app.mount('#app')
