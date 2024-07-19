SET max_parallel_workers_per_gather = 0;

--Arxiko Query
EXPLAIN ANALYZE
SELECT e."eduLevel", COUNT(*)
FROM education e
JOIN (
SELECT a.email
FROM advertisement a
JOIN "jobOffer" j ON a."advertisementID" = j."advertisementID"
WHERE a."datePosted" >= CURRENT_DATE - INTERVAL '6 months'
AND j."fromAge" > 21 AND j."toAge" < 30
GROUP BY a.email
HAVING COUNT(a."advertisementID") >= 2
) AS valid_ads ON e.email = valid_ads.email
JOIN (
SELECT m."receiverEmail"
FROM msg m
WHERE m."dateSent" >= CURRENT_DATE - INTERVAL '6 months'
GROUP BY m."receiverEmail"
) AS recent_msgs ON e.email = recent_msgs."receiverEmail"
WHERE e.country = 'Canada' --or El Salvador
GROUP BY e."eduLevel";

--Allagh seiras syndesewn
EXPLAIN ANALYZE
SELECT e."eduLevel", COUNT(*)
FROM education e
JOIN (
SELECT m."receiverEmail"
FROM msg m
WHERE m."dateSent" >= CURRENT_DATE - INTERVAL '6 months'
GROUP BY m."receiverEmail"
) AS recent_msgs ON e.email = recent_msgs."receiverEmail"
JOIN (
SELECT a.email
FROM advertisement a
JOIN "jobOffer" j ON a."advertisementID" = j."advertisementID"
WHERE a."datePosted" >= CURRENT_DATE - INTERVAL '6 months'
AND j."fromAge" > 21 AND j."toAge" < 30
GROUP BY a.email
HAVING COUNT(a."advertisementID") >= 2
) AS valid_ads ON e.email = valid_ads.email
WHERE e.country = 'Canada' --or El Salvador
GROUP BY e."eduLevel";

--Evrethria
CREATE INDEX education_email_idx ON education USING hash (email);
CREATE INDEX advertisement_date_idx ON advertisement("datePosted");
CREATE INDEX msg_date_idx ON msg("dateSent");

--Drop Evrethria
DROP INDEX IF EXISTS education_email_idx;
DROP INDEX IF EXISTS advertisement_date_idx
DROP INDEX IF EXISTS msg_date_idx

--Clusters
CLUSTER advertisement USING advertisement_date_idx;
CLUSTER msg USING msg_date_idx;

--Drop Clusters
ALTER TABLE advertisement SET WITHOUT CLUSTER;
ALTER TABLE msg SET WITHOUT CLUSTER;

--Joins on
SET enable_nestloop = on;
SET enable_hashjoin = on;
SET enable_mergejoin = on;

--Joins off
SET enable_nestloop = off;
SET enable_hashjoin = off;
SET enable_mergejoin = off;
