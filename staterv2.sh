#!/bin/bash

SRC=/home/net/projectdata/CAPECCWEAttackPatternsV2.owl 

echo "both:"
echo "CAPECs: " `cat $SRC | grep "Declaration(Class(:CAPEC_" | wc -l`
echo "CWEs: " `cat $SRC | grep "Declaration(Class(:CWE_" | wc -l`


echo "targetsCWE: " `cat $SRC | grep "ObjectHasValue(:targetsCWE" | wc -l`
echo "isTargetedBy: " `cat $SRC | grep "ObjectHasValue(:isTargetedsBy" | wc -l`


echo "impactsTechnicalImpact: " `cat $SRC | grep "ObjectHasValue(:impactsTechnicalImpact" | wc -l`
echo "scopes: " `cat $SRC | grep "ObjectHasValue(:scopes" | wc -l`

echo
echo "about CAPECs:"
echo "usesMethod: " `cat $SRC | grep "ObjectHasValue(:usesMethod" | wc -l`
echo "hasCAPECAbstraction: " `cat $SRC | grep "ObjectHasValue(:hasCAPECAbstraction" | wc -l`
echo "scoresLikelihood: " `cat $SRC | grep "DataHasValue(:scoresLikelihood" | wc -l`
echo "scoresSeverity: " `cat $SRC | grep "DataHasValue(:scoresSeverity" | wc -l`
echo "scoresRequeredSkill: " `cat $SRC | grep "DataHasValue(:scoresRequeredSkill" | wc -l`

echo
echo "about CWEs:"
echo "hasCWEAbstraction: " `cat $SRC | grep "ObjectHasValue(:hasCWEAbstraction" | wc -l`
echo "appearedAtPhase: " `cat $SRC | grep "ObjectHasValue(:appearedAtPhase" | wc -l`
echo "isDetectedBy: " `cat $SRC | grep "ObjectHasValue(:isDetectedBy" | wc -l`
echo "isMitigatedBy: " `cat $SRC | grep "ObjectHasValue(:isMitigatedBy" | wc -l`
echo "scoresExploitLikelihood: " `cat $SRC | grep "DataHasValue(:scoresExploitLikelihood" | wc -l`


