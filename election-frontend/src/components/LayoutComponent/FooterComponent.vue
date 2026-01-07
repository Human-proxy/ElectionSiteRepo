<script setup>
import IconHeart from "@/components/icons/IconHeart.vue";
import IconInstagram from "@/components/icons/IconInstagram.vue";
import IconGithub from "@/components/icons/IconGithub.vue";
import IconLink from "@/components/icons/IconLink.vue";
import IconPeopleBlue from "@/components/icons/IconPeopleBlue.vue";
import IconMail from "@/components/icons/IconMail.vue";
import IconTwitter from "@/components/icons/IconTwitter.vue";
import IconShield from "@/components/icons/IconShield.vue";
</script>
<template>
  <footer>
    <div class="footer-contents-all">
      <div class="footer-contents">
        <div class="footer-section">
          <div class="logo">
            <span class="logo-icon">NL</span>
            <span class="logo-name">StemWijs</span>

          </div>
          <p class="footer-content-description">
            Een onpartijdige verkiezingswebsite voor jongeren om geïnformeerde keuzes te maken voor de Nederlandse
            Tweede Kamerverkiezingen.
            <br>
            <br>
            Gemaakt met
            <IconHeart/>
            voor de democratie
          </p>
        </div>
        <div class="footer-section">
          <h4>
            Verken
          </h4>
          <a href="/home"><p class="footer-content-description">Homepagina</p></a>
          <a href="/results"><p class="footer-content-description">Verkiezingsresultaten</p></a>
          <a href="/over-ons"><p class="footer-content-description">Over ons</p></a>
          <a href="/leer"><p class="footer-content-description">Hoe werkt het?</p></a>
          <a href="/quiz"><p class="footer-content-description">Stemwijzer Quiz</p></a>
          <a href="/forum"><p class="footer-content-description">Forum</p></a>
        </div>
        <div class="footer-section">
          <h4>
            Contact
          </h4>
          <p class="footer-content-description">
            <IconMail/>
            info@stemwijs.nl
          </p>
          <p class="footer-content-description">
            <IconShield/>
            100% onpartijdig
          </p>
        </div>
      </div>
      <div class="important-msg-container">
        <h5>
          <IconPeopleBlue/>
        </h5>
        <p>
          Belangrijk bericht
          <br>
          <span class="footer-content-description">StemWijs is een onafhankelijke educatieve website en is niet gelieerd aan politieke partijen. De informatie dient ter oriëntatie en vervangt geen eigen onderzoek naar partijprogramma's.</span>
        </p>
      </div>
      <div
          class="footer-description-text flex text-gray-500 mt-8 mb-12 flex-col sm:flex-row justify-between items-center gap-4 text-xs text-muted-foreground">
        <div class="flex flex-wrap items-center gap-4">
          <span>© 2025 StemWijs. Alle rechten voorbehouden.</span><span>•</span><span>Ontwikkeld voor jongeren van 18-25 jaar</span>
        </div>
        <div class="flex items-center gap-4"><span class="flex items-center gap-1"><span>WCAG 2.1 AA<IconLink/></span></span><span>•</span><span>Nederlandse verkiezingen 2025</span>
        </div>
      </div>
    </div>
  </footer>
</template>
<style>
.important-msg-container h5 {
  display: flex;
  align-items: start;
  gap: 10px;
}

.important-msg-container {
  gap: 15px;
  display: flex;
  margin-top: 20px;
  width: 100%;
  padding: 15px;
  background: #f5f5f7;
  border-radius: 15px;
}

footer {
  margin-top: 150px;
  padding-top: 50px;
  display: flex;
  border-top: 2px solid #dbdbdb;
  justify-content: center;
  font-size: 14px;
}

.footer-content-description {
  color: gray;
  margin-bottom: 0.5rem;
}

.coming-soon-color {
  color: #aaa;
  cursor: not-allowed;
  opacity: 0.7;
}

.footer-contents-all {
  width: 1120px;
}

.footer-contents {
  display: flex;
  justify-content: center;
  width: 100%;
  gap: 2rem;
  padding-bottom: 45px;
  border-bottom: 1.5px solid #dbdbdb;
}

.footer-section {
  width: 256px;
}

.footer-section .logo {
  margin-bottom: 15px;
}

.follow-text {
  margin: 15px 0;
}

.footer-section h4 {
  margin-bottom: 15px;
}

.icon-gap {
  margin-right: 10px;
  display: inline;
}

@media only screen and (max-width: 835px) {
  .footer-contents {
    flex-direction: column;
    align-items: center;
  }

  .footer-section:nth-of-type(1), .footer-section:nth-of-type(2) {
    display: none;
  }

  .footer-section:nth-of-type(3) {
    width: 100%;
    text-align: center;
  }

  .important-msg-container {
    display: none;
  }

  .footer-description-text {
    text-align: center;
    flex-direction: column;
  }

  .footer-description-text div {
    text-align: center;
    display: flex;
    justify-content: center;
  }
}
</style>