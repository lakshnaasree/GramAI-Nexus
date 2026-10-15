package com.gramainexus.smartvillage.ui.screens

import android.content.Context
import android.content.Intent
import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.AccountBalance
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.BookmarkBorder
import androidx.compose.material.icons.filled.Business
import androidx.compose.material.icons.filled.BusinessCenter
import androidx.compose.material.icons.filled.Engineering
import androidx.compose.material.icons.filled.Groups
import androidx.compose.material.icons.filled.LocalHospital
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Mail
import androidx.compose.material.icons.filled.OpenInNew
import androidx.compose.material.icons.filled.Public
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material.icons.filled.Verified
import androidx.compose.material.icons.filled.Work
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.core.net.toUri

// ============================================================================
// COLORS
// ============================================================================

private val SmartGreen = Color(0xFF137A4B)
private val SmartGreenDark = Color(0xFF0B5535)
private val SmartGreenLight = Color(0xFFE8F5EE)

private val PageBackground = Color(0xFFF6F8F7)
private val CardWhite = Color.White

private val TextDark = Color(0xFF17211C)
private val TextGray = Color(0xFF68736D)

private val BorderLight = Color(0xFFE0E6E2)

private val PrivateChipBackground = Color(0xFFF1F3F5)


// ============================================================================
// DATA MODEL
// ============================================================================

private data class CareerPortal(
    val name: String,
    val tamilName: String,
    val description: String,
    val tamilDescription: String,
    val url: String,
    val category: String,
    val icon: ImageVector,
    val official: Boolean
)


// ============================================================================
// CAREER PORTALS
// ============================================================================

private val careerPortals = listOf(

    // ------------------------------------------------------------------------
    // GOVERNMENT & PUBLIC
    // ------------------------------------------------------------------------

    CareerPortal(
        name = "National Career Service",
        tamilName = "தேசிய தொழில் சேவை",
        description = "Government of India jobs, career services, counselling and employment opportunities.",
        tamilDescription = "அரசு வேலைகள், தொழில் ஆலோசனை மற்றும் வேலைவாய்ப்புகள்.",
        url = "https://www.ncs.gov.in/",
        category = "Government",
        icon = Icons.Default.Work,
        official = true
    ),

    CareerPortal(
        name = "UPSC",
        tamilName = "UPSC அரசு தேர்வுகள்",
        description = "Union Public Service Commission examinations and recruitment.",
        tamilDescription = "மத்திய அரசு பணிகளுக்கான UPSC தேர்வுகள்.",
        url = "https://www.upsc.gov.in/",
        category = "Government",
        icon = Icons.Default.AccountBalance,
        official = true
    ),

    CareerPortal(
        name = "UPSC Online",
        tamilName = "UPSC ஆன்லைன் விண்ணப்பம்",
        description = "Official UPSC online application and examination portal.",
        tamilDescription = "UPSC தேர்வுகளுக்கான அதிகாரப்பூர்வ ஆன்லைன் விண்ணப்ப தளம்.",
        url = "https://upsconline.nic.in/",
        category = "Government",
        icon = Icons.Default.AccountBalance,
        official = true
    ),

    CareerPortal(
        name = "Staff Selection Commission",
        tamilName = "SSC அரசு வேலைகள்",
        description = "Central government recruitment through SSC examinations.",
        tamilDescription = "SSC மூலம் மத்திய அரசு வேலைவாய்ப்புகள்.",
        url = "https://ssc.gov.in/",
        category = "Government",
        icon = Icons.Default.AccountBalance,
        official = true
    ),

    CareerPortal(
        name = "Railway Recruitment Boards",
        tamilName = "ரயில்வே வேலைவாய்ப்புகள்",
        description = "Official railway recruitment application portal.",
        tamilDescription = "இந்திய ரயில்வே வேலைவாய்ப்புகளுக்கான அதிகாரப்பூர்வ தளம்.",
        url = "https://www.rrbapply.gov.in/",
        category = "Government",
        icon = Icons.Default.Work,
        official = true
    ),

    CareerPortal(
        name = "India Post Vacancies",
        tamilName = "இந்தியா போஸ்ட் வேலைகள்",
        description = "Official India Post recruitment and vacancy information.",
        tamilDescription = "இந்தியா போஸ்ட் அதிகாரப்பூர்வ வேலைவாய்ப்பு அறிவிப்புகள்.",
        url = "https://www.indiapost.gov.in/vacancies",
        category = "Government",
        icon = Icons.Default.Mail,
        official = true
    ),

    CareerPortal(
        name = "Employment News",
        tamilName = "Employment News",
        description = "Government employment news and recruitment advertisements.",
        tamilDescription = "அரசு வேலைவாய்ப்பு செய்திகள் மற்றும் அறிவிப்புகள்.",
        url = "https://employmentnews.gov.in/",
        category = "Government",
        icon = Icons.Default.Work,
        official = true
    ),

    CareerPortal(
        name = "Apprenticeship India",
        tamilName = "Apprenticeship India",
        description = "Official apprenticeship opportunities across India.",
        tamilDescription = "இந்தியா முழுவதும் தொழில்பயிற்சி வாய்ப்புகள்.",
        url = "https://www.apprenticeshipindia.gov.in/",
        category = "Apprenticeships",
        icon = Icons.Default.Engineering,
        official = true
    ),

    CareerPortal(
        name = "National Apprenticeship Training Scheme",
        tamilName = "NATS தொழில்பயிற்சி",
        description = "Graduate and diploma apprenticeship opportunities.",
        tamilDescription = "பட்டதாரி மற்றும் டிப்ளமோ மாணவர்களுக்கான தொழில்பயிற்சி.",
        url = "https://nats.education.gov.in/",
        category = "Apprenticeships",
        icon = Icons.Default.School,
        official = true
    ),

    CareerPortal(
        name = "Skill India Digital",
        tamilName = "Skill India",
        description = "Government skill development, training and career opportunities.",
        tamilDescription = "அரசு திறன் பயிற்சி மற்றும் தொழில் வாய்ப்புகள்.",
        url = "https://www.skillindiadigital.gov.in/home",
        category = "Apprenticeships",
        icon = Icons.Default.School,
        official = true
    ),

    CareerPortal(
        name = "Tamil Nadu Career Services",
        tamilName = "தமிழ்நாடு தொழில் சேவை",
        description = "Tamil Nadu employment, apprenticeship and recruitment opportunities.",
        tamilDescription = "தமிழ்நாடு வேலைவாய்ப்பு மற்றும் தொழில்பயிற்சி வாய்ப்புகள்.",
        url = "https://tamilnaducareerservices.tn.gov.in/",
        category = "Tamil Nadu",
        icon = Icons.Default.LocationOn,
        official = true
    ),

    CareerPortal(
        name = "Tamil Nadu Employment Exchange",
        tamilName = "தமிழ்நாடு வேலைவாய்ப்பு அலுவலகம்",
        description = "Tamil Nadu employment exchange and registration services.",
        tamilDescription = "தமிழ்நாடு வேலைவாய்ப்பு பதிவு மற்றும் சேவைகள்.",
        url = "https://tnvelaivaaippu.gov.in/",
        category = "Tamil Nadu",
        icon = Icons.Default.LocationOn,
        official = true
    ),

    CareerPortal(
        name = "TNPSC",
        tamilName = "தமிழ்நாடு அரசுப் பணியாளர் தேர்வாணையம்",
        description = "Tamil Nadu government recruitment and competitive examinations.",
        tamilDescription = "தமிழ்நாடு அரசு பணிகளுக்கான தேர்வுகள்.",
        url = "https://www.tnpsc.gov.in/",
        category = "Tamil Nadu",
        icon = Icons.Default.AccountBalance,
        official = true
    ),

    CareerPortal(
        name = "TNUSRB",
        tamilName = "தமிழ்நாடு சீருடைப் பணிகள்",
        description = "Tamil Nadu uniformed services recruitment.",
        tamilDescription = "காவல் மற்றும் சீருடைப் பணிகளுக்கான ஆட்சேர்ப்பு.",
        url = "https://www.tnusrb.tn.gov.in/",
        category = "Tamil Nadu",
        icon = Icons.Default.Work,
        official = true
    ),

    CareerPortal(
        name = "Teachers Recruitment Board",
        tamilName = "ஆசிரியர் வேலைவாய்ப்புகள்",
        description = "Tamil Nadu teacher recruitment and examinations.",
        tamilDescription = "தமிழ்நாடு ஆசிரியர் ஆட்சேர்ப்பு அறிவிப்புகள்.",
        url = "https://trb.tn.gov.in/",
        category = "Tamil Nadu",
        icon = Icons.Default.School,
        official = true
    ),

    CareerPortal(
        name = "Medical Services Recruitment Board",
        tamilName = "மருத்துவ வேலைவாய்ப்புகள்",
        description = "Tamil Nadu medical and healthcare recruitment.",
        tamilDescription = "தமிழ்நாடு மருத்துவ மற்றும் சுகாதார வேலைவாய்ப்புகள்.",
        url = "https://www.mrb.tn.gov.in/",
        category = "Tamil Nadu",
        icon = Icons.Default.LocalHospital,
        official = true
    ),

    CareerPortal(
        name = "NCS Career Centres",
        tamilName = "NCS தொழில் மையங்கள்",
        description = "Government-supported career centres and employment services.",
        tamilDescription = "அரசு ஆதரவு தொழில் மையங்கள் மற்றும் வேலை சேவைகள்.",
        url = "https://ncs.gov.in/career-center",
        category = "Government",
        icon = Icons.Default.Groups,
        official = true
    ),

    CareerPortal(
        name = "India.gov Jobs",
        tamilName = "India.gov வேலை தகவல்கள்",
        description = "Government information about jobs, careers and employment services.",
        tamilDescription = "அரசு வேலை மற்றும் தொழில் தொடர்பான தகவல்கள்.",
        url = "https://www.india.gov.in/category/jobs",
        category = "Government",
        icon = Icons.Default.Public,
        official = true
    ),

    CareerPortal(
        name = "DRDO Careers",
        tamilName = "DRDO வேலைவாய்ப்புகள்",
        description = "Defence research jobs, internships, JRF and recruitment opportunities.",
        tamilDescription = "பாதுகாப்பு ஆராய்ச்சி, internship மற்றும் வேலைவாய்ப்புகள்.",
        url = "https://www.drdo.gov.in/drdo/offerings/vacancies",
        category = "Government",
        icon = Icons.Default.Engineering,
        official = true
    ),

    CareerPortal(
        name = "ISRO Careers",
        tamilName = "ISRO வேலைவாய்ப்புகள்",
        description = "Space research, engineering, science and technical opportunities.",
        tamilDescription = "விண்வெளி ஆராய்ச்சி மற்றும் தொழில்நுட்ப வேலைவாய்ப்புகள்.",
        url = "https://www.isro.gov.in/Careers.html",
        category = "Government",
        icon = Icons.Default.Public,
        official = true
    ),

    CareerPortal(
        name = "ISRO Current Opportunities",
        tamilName = "ISRO தற்போதைய வாய்ப்புகள்",
        description = "Current ISRO recruitment and opportunity notices.",
        tamilDescription = "ISRO தற்போதைய ஆட்சேர்ப்பு அறிவிப்புகள்.",
        url = "https://www.isro.gov.in/CurrentOpportunities.html",
        category = "Government",
        icon = Icons.Default.Public,
        official = true
    ),

    // ------------------------------------------------------------------------
    // BANKING & PSU
    // ------------------------------------------------------------------------

    CareerPortal(
        name = "RBI Opportunities",
        tamilName = "RBI வேலைவாய்ப்புகள்",
        description = "Reserve Bank of India recruitment and career opportunities.",
        tamilDescription = "இந்திய ரிசர்வ் வங்கி வேலைவாய்ப்புகள்.",
        url = "https://opportunities.rbi.org.in/Scripts/Vacancies.aspx",
        category = "Banking & PSU",
        icon = Icons.Default.AccountBalance,
        official = true
    ),

    CareerPortal(
        name = "IBPS",
        tamilName = "IBPS வங்கி வேலைகள்",
        description = "Banking recruitment examinations and participating bank vacancies.",
        tamilDescription = "வங்கி ஆட்சேர்ப்பு தேர்வுகள் மற்றும் வேலைவாய்ப்புகள்.",
        url = "https://www.ibps.in/",
        category = "Banking & PSU",
        icon = Icons.Default.AccountBalance,
        official = true
    ),

    CareerPortal(
        name = "State Bank of India Careers",
        tamilName = "SBI வேலைவாய்ப்புகள்",
        description = "Official SBI recruitment and career opportunities.",
        tamilDescription = "SBI அதிகாரப்பூர்வ வேலைவாய்ப்புகள்.",
        url = "https://sbi.co.in/web/careers",
        category = "Banking & PSU",
        icon = Icons.Default.AccountBalance,
        official = true
    ),

    CareerPortal(
        name = "Bank of Baroda Careers",
        tamilName = "Bank of Baroda வேலைகள்",
        description = "Official Bank of Baroda recruitment opportunities.",
        tamilDescription = "Bank of Baroda அதிகாரப்பூர்வ வேலைவாய்ப்புகள்.",
        url = "https://www.bankofbaroda.in/career/current-openings",
        category = "Banking & PSU",
        icon = Icons.Default.AccountBalance,
        official = true
    ),

    CareerPortal(
        name = "Canara Bank Careers",
        tamilName = "Canara Bank வேலைகள்",
        description = "Canara Bank recruitment and career information.",
        tamilDescription = "Canara Bank வேலைவாய்ப்பு தகவல்கள்.",
        url = "https://canarabank.com/pages/careers",
        category = "Banking & PSU",
        icon = Icons.Default.AccountBalance,
        official = true
    ),

    CareerPortal(
        name = "Indian Bank Careers",
        tamilName = "Indian Bank வேலைகள்",
        description = "Official Indian Bank recruitment information.",
        tamilDescription = "Indian Bank அதிகாரப்பூர்வ வேலைவாய்ப்புகள்.",
        url = "https://www.indianbank.in/career/",
        category = "Banking & PSU",
        icon = Icons.Default.AccountBalance,
        official = true
    ),

    CareerPortal(
        name = "Indian Overseas Bank Careers",
        tamilName = "IOB வேலைகள்",
        description = "Indian Overseas Bank recruitment and career opportunities.",
        tamilDescription = "Indian Overseas Bank வேலைவாய்ப்புகள்.",
        url = "https://www.iob.in/Careers",
        category = "Banking & PSU",
        icon = Icons.Default.AccountBalance,
        official = true
    ),

    CareerPortal(
        name = "Union Bank Careers",
        tamilName = "Union Bank வேலைகள்",
        description = "Union Bank of India recruitment opportunities.",
        tamilDescription = "Union Bank of India வேலைவாய்ப்புகள்.",
        url = "https://www.unionbankofindia.co.in/en/common/careers",
        category = "Banking & PSU",
        icon = Icons.Default.AccountBalance,
        official = true
    ),

    CareerPortal(
        name = "Punjab National Bank Careers",
        tamilName = "PNB வேலைகள்",
        description = "PNB recruitment and career information.",
        tamilDescription = "PNB அதிகாரப்பூர்வ வேலைவாய்ப்பு தகவல்கள்.",
        url = "https://www.pnbindia.in/careers.html",
        category = "Banking & PSU",
        icon = Icons.Default.AccountBalance,
        official = true
    ),

    CareerPortal(
        name = "NABARD Careers",
        tamilName = "NABARD வேலைகள்",
        description = "Rural development banking and recruitment opportunities.",
        tamilDescription = "கிராமப்புற வளர்ச்சி மற்றும் வங்கி வேலைவாய்ப்புகள்.",
        url = "https://www.nabard.org/careers-notices.aspx",
        category = "Banking & PSU",
        icon = Icons.Default.AccountBalance,
        official = true
    ),

    CareerPortal(
        name = "SEBI Careers",
        tamilName = "SEBI வேலைவாய்ப்புகள்",
        description = "Securities market regulator recruitment opportunities.",
        tamilDescription = "பங்குச் சந்தை ஒழுங்குமுறை வேலைவாய்ப்புகள்.",
        url = "https://www.sebi.gov.in/careers.html",
        category = "Banking & PSU",
        icon = Icons.Default.AccountBalance,
        official = true
    ),

    CareerPortal(
        name = "PFRDA Careers",
        tamilName = "PFRDA வேலைவாய்ப்புகள்",
        description = "Pension sector regulatory careers and recruitment.",
        tamilDescription = "ஓய்வூதியத் துறை வேலைவாய்ப்புகள்.",
        url = "https://www.pfrda.org.in/web/pfrda/careers",
        category = "Banking & PSU",
        icon = Icons.Default.AccountBalance,
        official = true
    ),

    CareerPortal(
        name = "IRDAI Careers",
        tamilName = "IRDAI வேலைவாய்ப்புகள்",
        description = "Insurance regulator recruitment and career opportunities.",
        tamilDescription = "காப்பீட்டு ஒழுங்குமுறை வேலைவாய்ப்புகள்.",
        url = "https://irdai.gov.in/careers",
        category = "Banking & PSU",
        icon = Icons.Default.AccountBalance,
        official = true
    ),

    CareerPortal(
        name = "EPFO Careers",
        tamilName = "EPFO வேலைகள்",
        description = "Employees' Provident Fund Organisation career opportunities.",
        tamilDescription = "EPFO அதிகாரப்பூர்வ வேலைவாய்ப்புகள்.",
        url = "https://www.epfindia.gov.in/site_en/Careers.php",
        category = "Banking & PSU",
        icon = Icons.Default.AccountBalance,
        official = true
    ),

    CareerPortal(
        name = "ESIC Recruitment",
        tamilName = "ESIC வேலைகள்",
        description = "Employees' State Insurance Corporation recruitment.",
        tamilDescription = "ESIC மருத்துவம் மற்றும் அரசு வேலைவாய்ப்புகள்.",
        url = "https://www.esic.gov.in/recruitments",
        category = "Banking & PSU",
        icon = Icons.Default.LocalHospital,
        official = true
    ),

    CareerPortal(
        name = "LIC Careers",
        tamilName = "LIC வேலைவாய்ப்புகள்",
        description = "Life Insurance Corporation of India careers.",
        tamilDescription = "LIC அதிகாரப்பூர்வ வேலைவாய்ப்புகள்.",
        url = "https://www.licindia.in/en/web/guest/careers",
        category = "Banking & PSU",
        icon = Icons.Default.Business,
        official = true
    ),

    CareerPortal(
        name = "BHEL Careers",
        tamilName = "BHEL வேலைகள்",
        description = "Engineering and public sector career opportunities.",
        tamilDescription = "BHEL பொறியியல் மற்றும் PSU வேலைவாய்ப்புகள்.",
        url = "https://careers.bhel.in/",
        category = "Banking & PSU",
        icon = Icons.Default.Engineering,
        official = true
    ),

    CareerPortal(
        name = "ONGC Careers",
        tamilName = "ONGC வேலைகள்",
        description = "Oil and natural gas sector career opportunities.",
        tamilDescription = "எண்ணெய் மற்றும் இயற்கை எரிவாயு துறை வேலைகள்.",
        url = "https://ongcindia.com/web/eng/career",
        category = "Banking & PSU",
        icon = Icons.Default.Engineering,
        official = true
    ),

    CareerPortal(
        name = "NTPC Careers",
        tamilName = "NTPC வேலைகள்",
        description = "Power generation and engineering careers.",
        tamilDescription = "மின்சாரத் துறை மற்றும் பொறியியல் வேலைகள்.",
        url = "https://careers.ntpc.co.in/",
        category = "Banking & PSU",
        icon = Icons.Default.Engineering,
        official = true
    ),

    CareerPortal(
        name = "GAIL Careers",
        tamilName = "GAIL வேலைகள்",
        description = "Natural gas and energy sector opportunities.",
        tamilDescription = "எரிசக்தி மற்றும் இயற்கை எரிவாயு வேலைகள்.",
        url = "https://www.gailonline.com/",
        category = "Banking & PSU",
        icon = Icons.Default.Engineering,
        official = true
    ),

    CareerPortal(
        name = "HAL Careers",
        tamilName = "HAL வேலைகள்",
        description = "Aerospace and defence manufacturing careers.",
        tamilDescription = "விமானவியல் மற்றும் பாதுகாப்புத் துறை வேலைகள்.",
        url = "https://hal-india.co.in/careers",
        category = "Banking & PSU",
        icon = Icons.Default.Engineering,
        official = true
    ),

    CareerPortal(
        name = "BEL Careers",
        tamilName = "BEL வேலைகள்",
        description = "Defence electronics and engineering recruitment.",
        tamilDescription = "பாதுகாப்பு மின்னணு மற்றும் பொறியியல் வேலைகள்.",
        url = "https://bel-india.in/careers/",
        category = "Banking & PSU",
        icon = Icons.Default.Engineering,
        official = true
    ),

    CareerPortal(
        name = "Coal India Careers",
        tamilName = "Coal India வேலைகள்",
        description = "Mining, engineering and public sector opportunities.",
        tamilDescription = "சுரங்கம், பொறியியல் மற்றும் PSU வேலைகள்.",
        url = "https://www.coalindia.in/career-cil/",
        category = "Banking & PSU",
        icon = Icons.Default.Engineering,
        official = true
    ),

    CareerPortal(
        name = "Indian Oil Careers",
        tamilName = "IOCL வேலைகள்",
        description = "Oil, gas and energy sector recruitment.",
        tamilDescription = "எண்ணெய் மற்றும் எரிசக்தித் துறை வேலைகள்.",
        url = "https://iocl.com/pages/careers-overview",
        category = "Banking & PSU",
        icon = Icons.Default.Engineering,
        official = true
    ),

    CareerPortal(
        name = "BPCL Careers",
        tamilName = "BPCL வேலைகள்",
        description = "Energy and petroleum sector career opportunities.",
        tamilDescription = "பெட்ரோலியம் மற்றும் எரிசக்தி வேலைகள்.",
        url = "https://www.bharatpetroleum.in/careers.aspx",
        category = "Banking & PSU",
        icon = Icons.Default.Engineering,
        official = true
    ),

    CareerPortal(
        name = "POWERGRID Careers",
        tamilName = "POWERGRID வேலைகள்",
        description = "Power transmission and engineering careers.",
        tamilDescription = "மின்சார பரிமாற்றம் மற்றும் பொறியியல் வேலைகள்.",
        url = "https://www.powergrid.in/en/job-opportunities",
        category = "Banking & PSU",
        icon = Icons.Default.Engineering,
        official = true
    ),

    CareerPortal(
        name = "NLC India Careers",
        tamilName = "NLC India வேலைகள்",
        description = "Mining, power and engineering opportunities.",
        tamilDescription = "சுரங்கம், மின்சாரம் மற்றும் பொறியியல் வேலைகள்.",
        url = "https://www.nlcindia.in/new_website/careers",
        category = "Banking & PSU",
        icon = Icons.Default.Engineering,
        official = true
    ),

    CareerPortal(
        name = "AAI Careers",
        tamilName = "Airport Authority வேலைகள்",
        description = "Airports Authority of India recruitment opportunities.",
        tamilDescription = "விமான நிலைய ஆணைய வேலைவாய்ப்புகள்.",
        url = "https://www.aai.aero/en/careers/recruitment",
        category = "Banking & PSU",
        icon = Icons.Default.Public,
        official = true
    ),

    // ------------------------------------------------------------------------
    // PRIVATE JOB PLATFORMS
    // ------------------------------------------------------------------------

    CareerPortal(
        name = "LinkedIn Jobs",
        tamilName = "LinkedIn வேலைகள்",
        description = "Professional jobs, internships and career opportunities.",
        tamilDescription = "தொழில்முறை வேலைகள் மற்றும் internship வாய்ப்புகள்.",
        url = "https://www.linkedin.com/jobs/",
        category = "Private Jobs",
        icon = Icons.Default.Groups,
        official = false
    ),

    CareerPortal(
        name = "Naukri",
        tamilName = "Naukri வேலைகள்",
        description = "Jobs across industries, experience levels and locations.",
        tamilDescription = "பல துறைகளில் வேலைவாய்ப்புகளைத் தேடுங்கள்.",
        url = "https://www.naukri.com/",
        category = "Private Jobs",
        icon = Icons.Default.Work,
        official = false
    ),

    CareerPortal(
        name = "Indeed India",
        tamilName = "Indeed வேலைகள்",
        description = "Search jobs by location, role and employer.",
        tamilDescription = "இடம் மற்றும் வேலை வகை அடிப்படையில் தேடுங்கள்.",
        url = "https://in.indeed.com/",
        category = "Private Jobs",
        icon = Icons.Default.Search,
        official = false
    ),

    CareerPortal(
        name = "Foundit",
        tamilName = "Foundit வேலைகள்",
        description = "Jobs, hiring and career opportunities across India.",
        tamilDescription = "இந்தியா முழுவதும் வேலைவாய்ப்புகள்.",
        url = "https://www.foundit.in/",
        category = "Private Jobs",
        icon = Icons.Default.Search,
        official = false
    ),

    CareerPortal(
        name = "Apna",
        tamilName = "Apna வேலைகள்",
        description = "Local and professional job opportunities.",
        tamilDescription = "உள்ளூர் மற்றும் தொழில்முறை வேலைவாய்ப்புகள்.",
        url = "https://apna.co/",
        category = "Private Jobs",
        icon = Icons.Default.Groups,
        official = false
    ),

    CareerPortal(
        name = "WorkIndia",
        tamilName = "WorkIndia வேலைகள்",
        description = "Jobs for freshers, experienced workers and local candidates.",
        tamilDescription = "புதியவர்கள் மற்றும் அனுபவமுள்ளவர்களுக்கான வேலைகள்.",
        url = "https://www.workindia.in/",
        category = "Private Jobs",
        icon = Icons.Default.Work,
        official = false
    ),

    CareerPortal(
        name = "Internshala",
        tamilName = "Internshala Internship",
        description = "Internships, work-from-home jobs and fresher opportunities.",
        tamilDescription = "Internship, வீட்டிலிருந்து வேலை மற்றும் fresher வாய்ப்புகள்.",
        url = "https://internshala.com/",
        category = "Internships",
        icon = Icons.Default.School,
        official = false
    ),

    CareerPortal(
        name = "Freshersworld",
        tamilName = "Freshersworld",
        description = "Jobs and recruitment opportunities for fresh graduates.",
        tamilDescription = "புதிய பட்டதாரிகளுக்கான வேலைவாய்ப்புகள்.",
        url = "https://www.freshersworld.com/",
        category = "Private Jobs",
        icon = Icons.Default.School,
        official = false
    ),

    CareerPortal(
        name = "Shine",
        tamilName = "Shine வேலைகள்",
        description = "Job search and career opportunities across India.",
        tamilDescription = "இந்தியா முழுவதும் வேலைவாய்ப்புகள்.",
        url = "https://www.shine.com/",
        category = "Private Jobs",
        icon = Icons.Default.Work,
        official = false
    ),

    CareerPortal(
        name = "TimesJobs",
        tamilName = "TimesJobs",
        description = "Professional jobs and recruitment opportunities.",
        tamilDescription = "தொழில்முறை வேலை மற்றும் ஆட்சேர்ப்பு வாய்ப்புகள்.",
        url = "https://www.timesjobs.com/",
        category = "Private Jobs",
        icon = Icons.Default.Work,
        official = false
    ),

    CareerPortal(
        name = "AmbitionBox Jobs",
        tamilName = "AmbitionBox வேலைகள்",
        description = "Jobs with company information and workplace insights.",
        tamilDescription = "நிறுவன தகவல்களுடன் வேலை தேடல்.",
        url = "https://www.ambitionbox.com/jobs",
        category = "Private Jobs",
        icon = Icons.Default.Business,
        official = false
    ),

    CareerPortal(
        name = "Cutshort",
        tamilName = "Cutshort வேலைகள்",
        description = "Technology, startup and professional job opportunities.",
        tamilDescription = "Technology மற்றும் startup வேலைவாய்ப்புகள்.",
        url = "https://cutshort.io/",
        category = "Private Jobs",
        icon = Icons.Default.BusinessCenter,
        official = false
    ),

    CareerPortal(
        name = "Wellfound",
        tamilName = "Wellfound Startup Jobs",
        description = "Startup jobs and technology career opportunities.",
        tamilDescription = "Startup மற்றும் technology வேலைவாய்ப்புகள்.",
        url = "https://wellfound.com/jobs",
        category = "Private Jobs",
        icon = Icons.Default.BusinessCenter,
        official = false
    ),

    CareerPortal(
        name = "iimjobs",
        tamilName = "iimjobs",
        description = "Professional and management career opportunities.",
        tamilDescription = "Management மற்றும் professional வேலைவாய்ப்புகள்.",
        url = "https://www.iimjobs.com/",
        category = "Private Jobs",
        icon = Icons.Default.BusinessCenter,
        official = false
    ),

    CareerPortal(
        name = "Hirist",
        tamilName = "Hirist Technology Jobs",
        description = "Technology, software and engineering jobs.",
        tamilDescription = "Software, technology மற்றும் engineering வேலைகள்.",
        url = "https://www.hirist.tech/",
        category = "Private Jobs",
        icon = Icons.Default.Engineering,
        official = false
    ),

    CareerPortal(
        name = "JobHai",
        tamilName = "JobHai",
        description = "Local jobs and entry-level employment opportunities.",
        tamilDescription = "உள்ளூர் மற்றும் entry-level வேலைவாய்ப்புகள்.",
        url = "https://www.jobhai.com/",
        category = "Private Jobs",
        icon = Icons.Default.Work,
        official = false
    ),

    CareerPortal(
        name = "Glassdoor Jobs",
        tamilName = "Glassdoor வேலைகள்",
        description = "Jobs with company reviews and career information.",
        tamilDescription = "நிறுவன மதிப்பீடுகளுடன் வேலை தேடல்.",
        url = "https://www.glassdoor.co.in/Job/index.htm",
        category = "Private Jobs",
        icon = Icons.Default.Business,
        official = false
    ),

    CareerPortal(
        name = "Instahyre",
        tamilName = "Instahyre",
        description = "Professional technology and hiring opportunities.",
        tamilDescription = "Technology மற்றும் professional வேலைவாய்ப்புகள்.",
        url = "https://www.instahyre.com/",
        category = "Private Jobs",
        icon = Icons.Default.BusinessCenter,
        official = false
    )
)


// ============================================================================
// MAIN SCREEN
// ============================================================================

@Composable
fun JobsScreen(
    selectedLanguage: String,
    villageName: String,
    onBack: () -> Unit
) {
    val context = LocalContext.current

    var searchText by remember {
        mutableStateOf("")
    }

    var selectedCategory by remember {
        mutableStateOf("All")
    }

    var savedPortals by remember {
        mutableStateOf(emptySet<String>())
    }

    val isTamil =
        selectedLanguage.equals("தமிழ்", ignoreCase = true) ||
                selectedLanguage.equals("Tamil", ignoreCase = true)

    val categories = listOf(
        "All",
        "Government",
        "Tamil Nadu",
        "Banking & PSU",
        "Apprenticeships",
        "Internships",
        "Private Jobs"
    )

    val filteredPortals = careerPortals.filter { portal ->

        val matchesCategory =
            selectedCategory == "All" ||
                    portal.category == selectedCategory

        val query = searchText.trim()

        val matchesSearch =
            query.isBlank() ||
                    portal.name.contains(query, ignoreCase = true) ||
                    portal.tamilName.contains(query, ignoreCase = true) ||
                    portal.description.contains(query, ignoreCase = true) ||
                    portal.category.contains(query, ignoreCase = true)

        matchesCategory && matchesSearch
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(PageBackground)
            .statusBarsPadding()
    ) {

        // ====================================================================
        // CUSTOM TOP BAR
        // ====================================================================

        JobsTopBar(
            isTamil = isTamil,
            onBack = onBack
        )

        // ====================================================================
        // CONTENT
        // ====================================================================

        LazyColumn(
            modifier = Modifier.fillMaxSize(),

            contentPadding = PaddingValues(
                start = 16.dp,
                top = 16.dp,
                end = 16.dp,
                bottom = 28.dp
            ),

            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {

            // ----------------------------------------------------------------
            // HERO
            // ----------------------------------------------------------------

            item {
                CareerHeroCard(
                    isTamil = isTamil,
                    villageName = villageName,
                    portalCount = careerPortals.size
                )
            }

            // ----------------------------------------------------------------
            // SEARCH
            // ----------------------------------------------------------------

            item {
                OutlinedTextField(
                    value = searchText,
                    onValueChange = {
                        searchText = it
                    },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    shape = RoundedCornerShape(16.dp),
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.Search,
                            contentDescription = null
                        )
                    },
                    trailingIcon = {
                        Icon(
                            imageVector = Icons.Default.Tune,
                            contentDescription = null,
                            tint = SmartGreen
                        )
                    },
                    placeholder = {
                        Text(
                            text = if (isTamil) {
                                "வேலை, நிறுவனம் அல்லது துறை தேடுங்கள்"
                            } else {
                                "Search jobs, companies or categories"
                            }
                        )
                    }
                )
            }

            // ----------------------------------------------------------------
            // CATEGORY FILTERS
            // ----------------------------------------------------------------

            item {
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {

                    items(categories) { category ->

                        val label = when {
                            !isTamil -> category

                            category == "All" ->
                                "அனைத்தும்"

                            category == "Government" ->
                                "அரசு"

                            category == "Tamil Nadu" ->
                                "தமிழ்நாடு"

                            category == "Banking & PSU" ->
                                "வங்கி & PSU"

                            category == "Apprenticeships" ->
                                "தொழில்பயிற்சி"

                            category == "Internships" ->
                                "Internship"

                            category == "Private Jobs" ->
                                "தனியார்"

                            else ->
                                category
                        }

                        CategoryChip(
                            text = label,
                            selected = selectedCategory == category,
                            onClick = {
                                selectedCategory = category
                            }
                        )
                    }
                }
            }

            // ----------------------------------------------------------------
            // RESULTS HEADER
            // ----------------------------------------------------------------

            item {

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {

                    Column(
                        modifier = Modifier.weight(1f)
                    ) {

                        Text(
                            text = if (isTamil) {
                                "${filteredPortals.size} வாய்ப்புகள்"
                            } else {
                                "${filteredPortals.size} opportunities"
                            },
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = TextDark
                        )

                        Spacer(
                            modifier = Modifier.height(2.dp)
                        )

                        Text(
                            text = if (isTamil) {
                                "அதிகாரப்பூர்வ மற்றும் வேலைவாய்ப்பு தளங்கள்"
                            } else {
                                "Official portals and job platforms"
                            },
                            style = MaterialTheme.typography.bodySmall,
                            color = TextGray
                        )
                    }

                    Surface(
                        shape = RoundedCornerShape(50.dp),
                        color = SmartGreenLight
                    ) {

                        Text(
                            text = if (isTamil) {
                                "நேரடி தளங்கள்"
                            } else {
                                "Live portals"
                            },
                            modifier = Modifier.padding(
                                start = 10.dp,
                                top = 6.dp,
                                end = 10.dp,
                                bottom = 6.dp
                            ),
                            color = SmartGreenDark,
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            // ----------------------------------------------------------------
            // PORTALS
            // ----------------------------------------------------------------

            items(
                items = filteredPortals,
                key = {
                    it.name
                }
            ) { portal ->

                CareerPortalCard(
                    portal = portal,
                    isTamil = isTamil,
                    isSaved = savedPortals.contains(portal.name),

                    onSave = {

                        savedPortals =
                            if (savedPortals.contains(portal.name)) {
                                savedPortals - portal.name
                            } else {
                                savedPortals + portal.name
                            }
                    },

                    onOpen = {
                        openCareerPortal(
                            context = context,
                            url = portal.url
                        )
                    }
                )
            }

            // ----------------------------------------------------------------
            // FOOTER
            // ----------------------------------------------------------------

            item {
                PortalFooter(
                    isTamil = isTamil
                )
            }
        }
    }
}


// ============================================================================
// TOP BAR
// ============================================================================

@Composable
private fun JobsTopBar(
    isTamil: Boolean,
    onBack: () -> Unit
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = SmartGreen,
        shadowElevation = 3.dp
    ) {

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(
                    start = 6.dp,
                    top = 10.dp,
                    end = 16.dp,
                    bottom = 12.dp
                ),
            verticalAlignment = Alignment.CenterVertically
        ) {

            IconButton(
                onClick = onBack
            ) {
                Text(
                    text = "‹",
                    color = Color.White,
                    style = MaterialTheme.typography.headlineLarge,
                    fontWeight = FontWeight.Light
                )
            }

            Spacer(
                modifier = Modifier.width(4.dp)
            )

            Column(
                modifier = Modifier.weight(1f)
            ) {

                Text(
                    text = if (isTamil) {
                        "வேலைவாய்ப்புகள்"
                    } else {
                        "Career Opportunities"
                    },
                    color = Color.White,
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )

                Text(
                    text = if (isTamil) {
                        "நம்பகமான வேலை மற்றும் தொழில் தளங்கள்"
                    } else {
                        "Trusted job & career portals"
                    },
                    color = Color.White.copy(alpha = 0.78f),
                    style = MaterialTheme.typography.labelSmall
                )
            }

            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(Color.White.copy(alpha = 0.14f)),
                contentAlignment = Alignment.Center
            ) {

                Icon(
                    imageVector = Icons.Default.Work,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(21.dp)
                )
            }
        }
    }
}


// ============================================================================
// CATEGORY CHIP
// ============================================================================

@Composable
private fun CategoryChip(
    text: String,
    selected: Boolean,
    onClick: () -> Unit
) {
    Surface(
        modifier = Modifier
            .clip(RoundedCornerShape(50.dp))
            .clickable {
                onClick()
            },
        shape = RoundedCornerShape(50.dp),
        color = if (selected) {
            SmartGreen
        } else {
            CardWhite
        },
        border = if (selected) {
            null
        } else {
            BorderStroke(
                width = 1.dp,
                color = BorderLight
            )
        }
    ) {

        Text(
            text = text,
            modifier = Modifier.padding(
                start = 15.dp,
                top = 9.dp,
                end = 15.dp,
                bottom = 9.dp
            ),
            color = if (selected) {
                Color.White
            } else {
                TextDark
            },
            style = MaterialTheme.typography.labelLarge,
            fontWeight = if (selected) {
                FontWeight.Bold
            } else {
                FontWeight.Medium
            }
        )
    }
}


// ============================================================================
// HERO
// ============================================================================

@Composable
private fun CareerHeroCard(
    isTamil: Boolean,
    villageName: String,
    portalCount: Int
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(
            containerColor = SmartGreen
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 3.dp
        )
    ) {

        Column(
            modifier = Modifier.padding(20.dp)
        ) {

            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {

                Box(
                    modifier = Modifier
                        .size(52.dp)
                        .clip(RoundedCornerShape(16.dp))
                        .background(
                            Color.White.copy(alpha = 0.16f)
                        ),
                    contentAlignment = Alignment.Center
                ) {

                    Icon(
                        imageVector = Icons.Default.Work,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(28.dp)
                    )
                }

                Spacer(
                    modifier = Modifier.width(14.dp)
                )

                Column(
                    modifier = Modifier.weight(1f)
                ) {

                    Text(
                        text = if (isTamil) {
                            "உங்கள் அடுத்த வாய்ப்பு"
                        } else {
                            "Your next opportunity"
                        },
                        color = Color.White,
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )

                    Spacer(
                        modifier = Modifier.height(3.dp)
                    )

                    Text(
                        text = if (isTamil) {

                            if (villageName.isBlank()) {
                                "அரசு, PSU மற்றும் தனியார் வேலைவாய்ப்புகள்"
                            } else {
                                "$villageName மற்றும் சுற்றுப்புற வேலைவாய்ப்புகள்"
                            }

                        } else {

                            if (villageName.isBlank()) {
                                "Government, PSU and private opportunities"
                            } else {
                                "Opportunities around $villageName"
                            }
                        },
                        color = Color.White.copy(alpha = 0.82f),
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }

            Spacer(
                modifier = Modifier.height(18.dp)
            )

            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {

                HeroStat(
                    number = "$portalCount+",
                    label = if (isTamil) {
                        "தளங்கள்"
                    } else {
                        "Portals"
                    }
                )

                HeroStat(
                    number = "7",
                    label = if (isTamil) {
                        "பிரிவுகள்"
                    } else {
                        "Categories"
                    }
                )

                HeroStat(
                    number = "24×7",
                    label = if (isTamil) {
                        "அணுகல்"
                    } else {
                        "Access"
                    }
                )
            }
        }
    }
}


// ============================================================================
// HERO STAT
// ============================================================================

@Composable
private fun HeroStat(
    number: String,
    label: String
) {
    Surface(
        shape = RoundedCornerShape(14.dp),
        color = Color.White.copy(alpha = 0.13f)
    ) {

        Column(
            modifier = Modifier.padding(
                start = 14.dp,
                top = 9.dp,
                end = 14.dp,
                bottom = 9.dp
            )
        ) {

            Text(
                text = number,
                color = Color.White,
                fontWeight = FontWeight.Bold,
                style = MaterialTheme.typography.titleMedium
            )

            Text(
                text = label,
                color = Color.White.copy(alpha = 0.76f),
                style = MaterialTheme.typography.labelSmall
            )
        }
    }
}


// ============================================================================
// CAREER PORTAL CARD
// ============================================================================

@Composable
private fun CareerPortalCard(
    portal: CareerPortal,
    isTamil: Boolean,
    isSaved: Boolean,
    onSave: () -> Unit,
    onOpen: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable {
                onOpen()
            },
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = CardWhite
        ),
        border = BorderStroke(
            width = 1.dp,
            color = BorderLight
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 1.dp
        )
    ) {

        Column(
            modifier = Modifier.padding(16.dp)
        ) {

            Row(
                verticalAlignment = Alignment.Top
            ) {

                Box(
                    modifier = Modifier
                        .size(50.dp)
                        .clip(RoundedCornerShape(15.dp))
                        .background(
                            if (portal.official) {
                                SmartGreenLight
                            } else {
                                PrivateChipBackground
                            }
                        ),
                    contentAlignment = Alignment.Center
                ) {

                    Icon(
                        imageVector = portal.icon,
                        contentDescription = null,
                        tint = if (portal.official) {
                            SmartGreen
                        } else {
                            Color(0xFF53615A)
                        },
                        modifier = Modifier.size(25.dp)
                    )
                }

                Spacer(
                    modifier = Modifier.width(13.dp)
                )

                Column(
                    modifier = Modifier.weight(1f)
                ) {

                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {

                        Text(
                            text = if (isTamil) {
                                portal.tamilName
                            } else {
                                portal.name
                            },
                            modifier = Modifier.weight(1f),
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = TextDark,
                            maxLines = 2,
                            overflow = TextOverflow.Ellipsis
                        )

                        IconButton(
                            onClick = onSave
                        ) {

                            Icon(
                                imageVector = if (isSaved) {
                                    Icons.Default.Bookmark
                                } else {
                                    Icons.Default.BookmarkBorder
                                },
                                contentDescription = if (isSaved) {
                                    "Saved"
                                } else {
                                    "Save"
                                },
                                tint = if (isSaved) {
                                    SmartGreen
                                } else {
                                    TextGray
                                }
                            )
                        }
                    }

                    Spacer(
                        modifier = Modifier.height(5.dp)
                    )

                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {

                        Surface(
                            shape = RoundedCornerShape(50.dp),
                            color = if (portal.official) {
                                SmartGreenLight
                            } else {
                                PrivateChipBackground
                            }
                        ) {

                            Row(
                                modifier = Modifier.padding(
                                    start = 8.dp,
                                    top = 5.dp,
                                    end = 8.dp,
                                    bottom = 5.dp
                                ),
                                verticalAlignment = Alignment.CenterVertically
                            ) {

                                if (portal.official) {

                                    Icon(
                                        imageVector = Icons.Default.Verified,
                                        contentDescription = null,
                                        modifier = Modifier.size(13.dp),
                                        tint = SmartGreen
                                    )

                                    Spacer(
                                        modifier = Modifier.width(4.dp)
                                    )
                                }

                                Text(
                                    text = when {
                                        portal.official && isTamil ->
                                            "அதிகாரப்பூர்வ தளம்"

                                        portal.official ->
                                            "Official portal"

                                        isTamil ->
                                            "வேலை தளம்"

                                        else ->
                                            "Job platform"
                                    },
                                    style = MaterialTheme.typography.labelSmall,
                                    color = if (portal.official) {
                                        SmartGreenDark
                                    } else {
                                        TextGray
                                    },
                                    fontWeight = FontWeight.SemiBold
                                )
                            }
                        }

                        Spacer(
                            modifier = Modifier.width(7.dp)
                        )

                        Text(
                            text = portal.category,
                            style = MaterialTheme.typography.labelSmall,
                            color = TextGray,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }
            }

            Spacer(
                modifier = Modifier.height(13.dp)
            )

            // Simple divider instead of experimental Material divider APIs
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(1.dp)
                    .background(BorderLight)
            )

            Spacer(
                modifier = Modifier.height(12.dp)
            )

            Text(
                text = if (isTamil) {
                    portal.tamilDescription
                } else {
                    portal.description
                },
                style = MaterialTheme.typography.bodyMedium,
                color = TextGray,
                maxLines = 3,
                overflow = TextOverflow.Ellipsis
            )

            Spacer(
                modifier = Modifier.height(14.dp)
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {

                Text(
                    text = if (isTamil) {
                        "தளத்தை திறக்கவும்"
                    } else {
                        "Open website"
                    },
                    color = SmartGreen,
                    fontWeight = FontWeight.Bold,
                    style = MaterialTheme.typography.labelLarge
                )

                Spacer(
                    modifier = Modifier.weight(1f)
                )

                Icon(
                    imageVector = Icons.Default.OpenInNew,
                    contentDescription = null,
                    tint = SmartGreen,
                    modifier = Modifier.size(19.dp)
                )

                Spacer(
                    modifier = Modifier.width(6.dp)
                )

                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                    contentDescription = null,
                    tint = SmartGreen,
                    modifier = Modifier.size(19.dp)
                )
            }
        }
    }
}


// ============================================================================
// FOOTER
// ============================================================================

@Composable
private fun PortalFooter(
    isTamil: Boolean
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFFEFF5F1)
        )
    ) {

        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.Top
        ) {

            Icon(
                imageVector = Icons.Default.Verified,
                contentDescription = null,
                tint = SmartGreen,
                modifier = Modifier.size(22.dp)
            )

            Spacer(
                modifier = Modifier.width(10.dp)
            )

            Column {

                Text(
                    text = if (isTamil) {
                        "பாதுகாப்பான வேலை தேடல்"
                    } else {
                        "Safer job searching"
                    },
                    color = TextDark,
                    fontWeight = FontWeight.Bold
                )

                Spacer(
                    modifier = Modifier.height(4.dp)
                )

                Text(
                    text = if (isTamil) {
                        "விண்ணப்பிக்கும் முன் அந்த இணையதளத்தில் வேலை அறிவிப்பு மற்றும் தகுதிகளை சரிபார்க்கவும்."
                    } else {
                        "Before applying, verify the vacancy, eligibility and application details on the website."
                    },
                    color = TextGray,
                    style = MaterialTheme.typography.bodySmall
                )
            }
        }
    }
}


// ============================================================================
// OPEN WEBSITE
// ============================================================================

private fun openCareerPortal(
    context: Context,
    url: String
) {
    try {

        val intent = Intent(
            Intent.ACTION_VIEW,
            url.toUri()
        )

        context.startActivity(intent)

    } catch (_: Exception) {

        Toast.makeText(
            context,
            "No browser app found",
            Toast.LENGTH_SHORT
        ).show()
    }
}