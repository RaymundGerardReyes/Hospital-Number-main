package Systems.Consultation;

import java.util.HashMap;
import java.util.Map;

public class HealthConcernHandler {

    private Map<String, String> healthConcernToSpecialistMap;

    public HealthConcernHandler() {
        healthConcernToSpecialistMap = new HashMap<>();
        initializeSpecialistMap();
    }

    private void initializeSpecialistMap() {
        healthConcernToSpecialistMap.put("hypothyroidism", "Endocrinologist");
        healthConcernToSpecialistMap.put("hyperthyroidism", "Endocrinologist");
        healthConcernToSpecialistMap.put("diabetes", "Endocrinologist");
        healthConcernToSpecialistMap.put("adrenal insufficiency", "Endocrinologist");
        healthConcernToSpecialistMap.put("obesity", "Endocrinologist");
        
        healthConcernToSpecialistMap.put("high blood pressure", "Cardiologist");
        healthConcernToSpecialistMap.put("heart attack", "Cardiologist");
        healthConcernToSpecialistMap.put("heart failure", "Cardiologist");
        healthConcernToSpecialistMap.put("arrhythmia", "Cardiologist");
        healthConcernToSpecialistMap.put("chest pain", "Cardiologist");
        
        healthConcernToSpecialistMap.put("asthma", "Pulmonologist");
        healthConcernToSpecialistMap.put("chronic obstructive pulmonary disease", "Pulmonologist");
        healthConcernToSpecialistMap.put("lung cancer", "Pulmonologist");
        healthConcernToSpecialistMap.put("sleep apnea", "Pulmonologist");
        healthConcernToSpecialistMap.put("tuberculosis", "Pulmonologist");
        
        healthConcernToSpecialistMap.put("depression", "Psychiatrist");
        healthConcernToSpecialistMap.put("anxiety", "Psychiatrist");
        healthConcernToSpecialistMap.put("bipolar disorder", "Psychiatrist");
        healthConcernToSpecialistMap.put("schizophrenia", "Psychiatrist");
        healthConcernToSpecialistMap.put("autism", "Psychiatrist");
        healthConcernToSpecialistMap.put("adhd", "Psychiatrist");
        
        healthConcernToSpecialistMap.put("arthritis", "Rheumatologist");
        healthConcernToSpecialistMap.put("lupus", "Rheumatologist");
        healthConcernToSpecialistMap.put("fibromyalgia", "Rheumatologist");
        healthConcernToSpecialistMap.put("gout", "Rheumatologist");
        healthConcernToSpecialistMap.put("osteoporosis", "Rheumatologist");
        
        healthConcernToSpecialistMap.put("skin rash", "Dermatologist");
        healthConcernToSpecialistMap.put("acne", "Dermatologist");
        healthConcernToSpecialistMap.put("psoriasis", "Dermatologist");
        healthConcernToSpecialistMap.put("eczema", "Dermatologist");
        healthConcernToSpecialistMap.put("melanoma", "Dermatologist");
        
        healthConcernToSpecialistMap.put("back pain", "Orthopedist");
        healthConcernToSpecialistMap.put("knee pain", "Orthopedist");
        healthConcernToSpecialistMap.put("hip pain", "Orthopedist");
        healthConcernToSpecialistMap.put("shoulder pain", "Orthopedist");
        healthConcernToSpecialistMap.put("fractures", "Orthopedist");
        
        healthConcernToSpecialistMap.put("chronic pain", "Pain Management Specialist");
        healthConcernToSpecialistMap.put("migraine", "Pain Management Specialist");
        healthConcernToSpecialistMap.put("neuropathy", "Pain Management Specialist");
        healthConcernToSpecialistMap.put("fibromyalgia", "Pain Management Specialist");
        healthConcernToSpecialistMap.put("sciatica", "Pain Management Specialist");
        
        healthConcernToSpecialistMap.put("stroke", "Neurologist");
        healthConcernToSpecialistMap.put("seizures", "Neurologist");
        healthConcernToSpecialistMap.put("parkinson's disease", "Neurologist");
        healthConcernToSpecialistMap.put("multiple sclerosis", "Neurologist");
        healthConcernToSpecialistMap.put("dementia", "Neurologist");
        
        healthConcernToSpecialistMap.put("pregnancy", "Obstetrician/Gynecologist");
        healthConcernToSpecialistMap.put("menstrual problems", "Gynecologist");
        healthConcernToSpecialistMap.put("menopause", "Gynecologist");
        healthConcernToSpecialistMap.put("infertility", "Gynecologist");
        healthConcernToSpecialistMap.put("ovarian cysts", "Gynecologist");
        
        healthConcernToSpecialistMap.put("urinary tract infection", "Urologist");
        healthConcernToSpecialistMap.put("kidney stones", "Urologist");
        healthConcernToSpecialistMap.put("prostate issues", "Urologist");
        healthConcernToSpecialistMap.put("bladder problems", "Urologist");
        healthConcernToSpecialistMap.put("incontinence", "Urologist");
        
        healthConcernToSpecialistMap.put("liver disease", "Hepatologist");
        healthConcernToSpecialistMap.put("hepatitis", "Hepatologist");
        healthConcernToSpecialistMap.put("cirrhosis", "Hepatologist");
        healthConcernToSpecialistMap.put("fatty liver disease", "Hepatologist");
        healthConcernToSpecialistMap.put("liver cancer", "Hepatologist");
        
        healthConcernToSpecialistMap.put("stomach pain", "Gastroenterologist");
        healthConcernToSpecialistMap.put("constipation", "Gastroenterologist");
        healthConcernToSpecialistMap.put("irritable bowel syndrome", "Gastroenterologist");
        healthConcernToSpecialistMap.put("crohn's disease", "Gastroenterologist");
        healthConcernToSpecialistMap.put("ulcerative colitis", "Gastroenterologist");
        
        healthConcernToSpecialistMap.put("hearing loss", "Otolaryngologist");
        healthConcernToSpecialistMap.put("sinusitis", "Otolaryngologist");
        healthConcernToSpecialistMap.put("tonsillitis", "Otolaryngologist");
        healthConcernToSpecialistMap.put("ear infection", "Otolaryngologist");
        healthConcernToSpecialistMap.put("vertigo", "Otolaryngologist");
        
        healthConcernToSpecialistMap.put("vision problems", "Ophthalmologist");
        healthConcernToSpecialistMap.put("glaucoma", "Ophthalmologist");
        healthConcernToSpecialistMap.put("cataracts", "Ophthalmologist");
        healthConcernToSpecialistMap.put("macular degeneration", "Ophthalmologist");
        healthConcernToSpecialistMap.put("diabetic retinopathy", "Ophthalmologist");
        
        healthConcernToSpecialistMap.put("cancer", "Oncologist");
        healthConcernToSpecialistMap.put("breast cancer", "Oncologist");
        healthConcernToSpecialistMap.put("lung cancer", "Oncologist");
        healthConcernToSpecialistMap.put("prostate cancer", "Oncologist");
        healthConcernToSpecialistMap.put("colorectal cancer", "Oncologist");
        
        healthConcernToSpecialistMap.put("allergies", "Allergist/Immunologist");
        healthConcernToSpecialistMap.put("food allergies", "Allergist/Immunologist");
        healthConcernToSpecialistMap.put("hay fever", "Allergist/Immunologist");
        healthConcernToSpecialistMap.put("asthma", "Allergist/Immunologist");
        healthConcernToSpecialistMap.put("eczema", "Allergist/Immunologist");
        
        healthConcernToSpecialistMap.put("insomnia", "Sleep Specialist");
        healthConcernToSpecialistMap.put("sleep apnea", "Sleep Specialist");
        healthConcernToSpecialistMap.put("narcolepsy", "Sleep Specialist");
        healthConcernToSpecialistMap.put("restless legs syndrome", "Sleep Specialist");
        healthConcernToSpecialistMap.put("shift work disorder", "Sleep Specialist");
        
        healthConcernToSpecialistMap.put("anemia", "Hematologist");
        healthConcernToSpecialistMap.put("leukemia", "Hematologist");
        healthConcernToSpecialistMap.put("lymphoma", "Hematologist");
        healthConcernToSpecialistMap.put("hemophilia", "Hematologist");
        healthConcernToSpecialistMap.put("thrombocytopenia", "Hematologist");
    }

    public String getSpecialist(String healthConcern) {
        return healthConcernToSpecialistMap.getOrDefault(healthConcern.toLowerCase(), "Family Medicine");
    }
}
