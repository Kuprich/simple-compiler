import { createApp } from 'vue'
import App from './App.vue'
import './assets/css/main.css'
import PrimeVue from 'primevue/config'
import Aura from '@primeuix/themes/aura'

// PrimeVue icons
import 'primeicons/primeicons.css'

const app = createApp(App)

app.use(PrimeVue, {
  theme: {
    preset: Aura,
    options: {
      darkModeSelector: '.my-app-dark'
    }
  }
});


app.mount('#app')
