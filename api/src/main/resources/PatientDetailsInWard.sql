INSERT INTO global_property (`property`, `property_value`, `description`, `uuid`)
VALUES ('bedManagement.sqlGet.patientListForAdmissionLocation',
"SELECT
    bed.bed_number AS 'Bed Number',
    bed_tags.bed_tags AS 'Bed Tags',
    bed.status as 'Bed Status',
    patient_identifier.identifier AS 'Patient ID',
    CONCAT(pn.given_name, ' ', pn.family_name)  AS 'Patient Name',
    TIMESTAMPDIFF(YEAR, p.birthdate, CURDATE()) AS 'Age'
FROM bed
    INNER JOIN bed_location_map blm ON blm.bed_id = bed.bed_id
    INNER JOIN location l ON l.location_id = blm.location_id AND l.retired IS FALSE
    LEFT OUTER JOIN bed_patient_assignment_map bpam ON bpam.bed_id = bed.bed_id AND bpam.date_stopped IS NULL
    LEFT OUTER JOIN person p ON p.person_id = bpam.patient_id AND p.voided IS FALSE
    LEFT OUTER JOIN person_name pn ON pn.person_id = p.person_id AND pn.voided IS FALSE
    LEFT OUTER JOIN patient_identifier ON patient_identifier.patient_id = p.person_id AND patient_identifier.voided IS FALSE
    LEFT OUTER JOIN person_address address ON address.person_id = p.person_id AND address.voided IS FALSE
    LEFT OUTER JOIN (
        SELECT bed_tag_map.bed_id AS 'bed_id', GROUP_CONCAT(DISTINCT bed_tag.name ORDER BY bed_tag.name) AS 'bed_tags'
        FROM bed_tag_map
        INNER JOIN bed_tag ON bed_tag.bed_tag_id = bed_tag_map.bed_tag_id AND bed_tag_map.voided IS FALSE
        GROUP BY bed_tag_map.bed_id
    ) bed_tags ON bed_tags.bed_id = bed.bed_id
WHERE  l.name = ${location_name}
GROUP BY bed.bed_number;",
'Sql query to get admitted patients details in an Admission Location',
uuid());