import { createApp } from 'vue'
import App from './App.vue'
import './assets/css/main.css'
import PrimeVue from 'primevue/config'
import ConfirmationService from 'primevue/confirmationservice';
import Aura from '@primeuix/themes/aura'
import { useTheme } from './composables/useTheme'

// PrimeVue icons
import 'primeicons/primeicons.css'

const {THEME_CLASS, applyTheme} = useTheme()

const app = createApp(App)

applyTheme()


app.use(PrimeVue, {
  theme: {
    preset: Aura,
    options: {
      darkModeSelector: `.${THEME_CLASS}`
    }
  }
})

app.use(ConfirmationService)


app.mount('#app')
